const http = require('http');

let jarId = 0;
const cookieJar = [{}, {}, {}, {}, {}];
const getCookies = () => {
  const jar = cookieJar[jarId];
  let s = [];
  for (let key in jar) s.push(`${key}=${jar[key]}`);
  return s.join('; ');
};

const getToken = (params) => {
  if (params) {
    const token = params.token;
    delete params.token;
    return token;
  } else {
    return null;
  }
};

const asyncGet = (url, params) => new Promise((resolve, reject) => {
  const token = getToken(params);
  if (params) url += '?' + (new URLSearchParams(params)).toString();
  const headers = {
    'Authorization': token ? `Bearer ${token}` : '',
    'Cookie': getCookies(),
  };
  const req = http.request(url, { headers }, (res) => {
    res.setEncoding('utf8');
    const data = [];
    res.on('data', (chunk) => data.push(chunk));
    res.on('end', () => resolve([res.statusCode, data.join(''), res.headers]));
  });
  req.end();
});

const asyncPost = (url, params) => new Promise((resolve, reject) => {
  const token = getToken(params);
  const body = (new URLSearchParams(params)).toString();
  const opt = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Content-Length': Buffer.byteLength(body),
      'Authorization': token ? `Bearer ${token}` : '',
      'Cookie': getCookies(),
    },
  };
  const req = http.request(url, opt, (res) => {
    res.setEncoding('utf8');
    const data = [];
    res.on('data', (chunk) => data.push(chunk));
    res.on('end', () => resolve([res.statusCode, data.join(''), res.headers]));
  });
  req.write(body);
  req.end();
});

const any = {};
const objEqual = (a, b) => {
  if (a === any || b === any) return true;
  if (typeof a !== typeof b) return false;
  if (typeof a !== 'object') return a === b;
  if ((a === null) ^ (b === null)) return false;

  for (let key in a) if (!objEqual(a[key], b[key])) return false;
  for (let key in b) if (!objEqual(a[key], b[key])) return false;
  return true;
};

let total = 0, pass = 0;

const check = async (method, url, params, expect, expect_status) => {
  const baseUrl = process.env['HOST'] || 'http://localhost:2317';
  const info = `${method + ' '.repeat(4 - method.length)} ` +
    `${url + ' '.repeat(25 - url.length)} | `;
  const [status, response, headers] =
    await (method === 'GET' ? asyncGet : asyncPost)(baseUrl + url, params);

  // Handle cookies
  const setCookie = headers['set-cookie'];
  if (setCookie) for (let i = 0; i < setCookie.length; i++) {
    let s = setCookie[i];
    const p = s.indexOf(';');
    if (p !== -1) s = s.substring(0, p);
    const [key, value] = s.split('=');
    cookieJar[jarId][key] = value;
  }

  let actual = undefined;
  let result = '';
  let correct = true;
  let compare = false;

  if (status !== (expect_status || 200)) {
    result = `Incorrect status ${status}`;
    correct = false;
    compare = true;
  }
  try {
    actual = JSON.parse(response);
  } catch (e) {
    if (expect !== undefined) {
      result = `Incorrect JSON format ${status} ${response.trimEnd()}`;
      correct = false;
    } else if (correct) {
      result = `Correct ${status} empty response`;
    }
  }
  if (correct) {
    if (expect !== undefined) {
      if (objEqual(expect, actual)) {
        const s = JSON.stringify(actual);
        result = `Correct ${status} ${s.substring(0, 50)}${s.length > 50 ? '...' : ''}`;
      } else {
        result = `Incorrect response content`;
        correct = false;
        compare = true;
      }
    } else if (actual !== undefined) {
      const s = JSON.stringify(actual);
      result = `Incorrect non-empty response ${s.substring(0, 50)}${s.length > 50 ? '...' : ''}`;
      correct = false;
    }
  }

  console.log(info + result);
  if (!correct) {
    console.log('Params: ', params);
    if (compare) {
      console.log('Expect: ', expect);
      console.log('Actual: ', actual);
      console.log();
    }
  }

  total += 1;
  pass += (correct ? 1 : 0);

  return actual || {};
};

(async () => {
  await check('POST', '/reset')

/*
  console.log('======== Sign up ========');
  await check('POST', '/signup', {nickname: 'kayuyuko1', email: 'kyyk1@kawa..moe', password: '888888'}, {error: 1}, 400)
  await check('POST', '/signup', {nickname: 'kayuyuko1', email: 'kyyk1@kawa.moe', password: '哈哈哈哈哈哈'}, {error: 1}, 400)
  await check('POST', '/signup', {nickname: 'kayuyuko1', email: 'kyyk1@kawa.moe', password: '888'}, {error: 1}, 400)
  await check('POST', '/signup', {nickname: 'kayuyuko1', email: 'kyyk1@kawa.moe', password: '888888'}, {error: 0}, 200)
  await check('POST', '/signup', {nickname: 'kayuyuko1', email: 'kyyk2@kawa.moe', password: '888888'}, {error: 2}, 400)
  await check('POST', '/signup', {nickname: 'kayuyuko2', email: 'kyyk1@kawa.moe', password: '888888'}, {error: 3}, 400)
  await check('POST', '/signup', {nickname: 'kayuyuko2', email: 'kyyk2@kawa.moe', password: '888888'}, {error: 0}, 200)
  await check('POST', '/signup', {nickname: '小猫', email: 'kurikoneko@kawa.moe', password: '888888'}, {error: 1}, 400)
  await check('POST', '/signup', {nickname: '栗小猫', email: 'kurikoneko@kawa.moe', password: '888888'}, {error: 0}, 200)
*/

  await check('POST', '/signup', {nickname: 'kayuyuko', email: 'kyyk@kawa.moe', password: 'P4$$w0rd'}, {error: 0}, 200)
  await check('POST', '/signup', {nickname: 'kurikoneko', email: 'kuriko@example.com', password: 'letme1n'}, {error: 0}, 200)
  await check('POST', '/login', {nickname: 'doesnotexist', password: '888888'}, undefined, 400)
  await check('POST', '/login', {nickname: 'kayuyuko', password: '888888'}, undefined, 400)
  let token1 = (await check('POST', '/login', {nickname: 'kayuyuko', password: 'P4$$w0rd'}, {
    token: any,
    user: {nickname: 'kayuyuko', avatar: '', signature: ''}
  }, 200)).token
  await check('GET', '/whoami', {token: '123123'}, {}, 400)
  await check('GET', '/whoami', {token: token1}, {nickname: 'kayuyuko', avatar: '', signature: ''})
  let token2 = (await check('POST', '/login', {nickname: 'kurikoneko', password: 'letme1n'}, {
    token: any,
    user: {nickname: 'kurikoneko', avatar: '', signature: ''}
  }, 200)).token

  // Posts
  await check('POST', '/post/new', {
    token: token2,
    type: 0,
    caption: '今天是甜粥粥。',
    contents: 'Lorem ipsum',
    tags: '美食,狗粮,每周粥粥',
    publish: 1,
  }, {id: any})
  let pid1 = (await check('POST', '/post/new', {
    token: token1,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    tags: 'tag1,tag2',
    publish: 1,
  }, {id: any})).id
  let no_pid = pid1 + 10;
  await check('GET', `/post/${pid1}`, undefined, {
    author: {nickname: 'kayuyuko', avatar: ''},
    timestamp: any,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    tags: ['tag1', 'tag2'],
    upvote_count: 0,
    comment_count: 0,
    mark_count: 0,
  })
  await check('GET', `/post/${no_pid}`, undefined, undefined, 404)

  // Comments
  let cid1 = (await check('POST', `/post/${pid1}/comment/new`, {
    token: token2,
    reply_to: -1,
    contents: 'No comment',
  }, {id: any})).id
  let no_cid = cid1 + 10
  await check('POST', `/post/${no_pid}/comment/new`, {
    token: token1,
    reply_to: -1,
    contents: 'No comment',
  }, undefined, 400)
  await check('POST', `/post/${pid1}/comment/new`, {
    token: token1,
    reply_to: no_cid,
    contents: 'No comment',
  }, undefined, 400)
  let cid2 = (await check('POST', `/post/${pid1}/comment/new`, {
    token: token1,
    reply_to: cid1,
    contents: 'Yes comment',
  }, {id: any})).id
  let cid3 = (await check('POST', `/post/${pid1}/comment/new`, {
    token: token2,
    reply_to: cid2,
    contents: 'Unknown comment',
  }, {id: any})).id
  let cid4 = (await check('POST', `/post/${pid1}/comment/new`, {
    token: token1,
    reply_to: -1,
    contents: 'Another yes comment',
  }, {id: any})).id

  await check('GET', `/post/${pid1}/comments`, {
    token: token1,
    start: 0,
    count: 10,
  }, [
    {id: cid4, author: {nickname: 'kayuyuko', avatar: ''}, timestamp: any, reply_user: null, contents: 'Another yes comment'},
    {id: cid1, author: {nickname: 'kurikoneko', avatar: ''}, timestamp: any, reply_user: null, contents: 'No comment'},
  ])
  await check('GET', `/post/${pid1}/comments`, {
    token: token1,
    start: 0,
    count: 10,
    reply_root: cid1,
  }, [
    {id: cid3, author: {nickname: 'kurikoneko', avatar: ''}, timestamp: any, reply_user: {nickname: 'kayuyuko', avatar: ''}, contents: 'Unknown comment'},
    {id: cid2, author: {nickname: 'kayuyuko', avatar: ''}, timestamp: any, reply_user: {nickname: 'kurikoneko', avatar: ''}, contents: 'Yes comment'},
  ])
  await check('GET', `/post/${pid1}/comments`, {
    token: token1,
    start: 1,
    count: 1,
    reply_root: cid1,
  }, [
    {id: cid2, author: {nickname: 'kayuyuko', avatar: ''}, timestamp: any, reply_user: {nickname: 'kurikoneko', avatar: ''}, contents: 'Yes comment'},
  ])

  // Upvote
  await check('POST', `/post/${pid1}/upvote`, {token: token1, is_upvote: 1}, {upvote_count: 1})
  await check('POST', `/post/${pid1}/upvote`, {token: token1, is_upvote: 0}, {upvote_count: 0})
  await check('POST', `/post/${pid1}/upvote`, {token: token1, is_upvote: 0}, {upvote_count: 0})
  await check('POST', `/post/${pid1}/upvote`, {token: token2, is_upvote: 0}, {upvote_count: 0})
  await check('POST', `/post/${pid1}/upvote`, {token: token2, is_upvote: 1}, {upvote_count: 1})
  await check('POST', `/post/${pid1}/upvote`, {token: token1, is_upvote: 1}, {upvote_count: 2})
  await check('POST', `/post/${pid1}/upvote`, {token: token1, is_upvote: 1}, {upvote_count: 2})
  await check('POST', `/post/${pid1}/upvote`, {token: token2, is_upvote: 0}, {upvote_count: 1})
  await check('POST', `/post/${no_pid}/upvote`, {token: token2, is_upvote: 1}, undefined, 400)

  // Mark
  await check('POST', `/post/${pid1}/mark`, {token: token1, is_mark: 0}, {mark_count: 0})
  await check('POST', `/post/${pid1}/mark`, {token: token1, is_mark: 1}, {mark_count: 1})
  await check('POST', `/post/${pid1}/mark`, {token: token2, is_mark: 1}, {mark_count: 2})
  await check('POST', `/post/${pid1}/mark`, {token: token2, is_mark: 1}, {mark_count: 2})
  await check('POST', `/post/${pid1}/mark`, {token: token2, is_mark: 0}, {mark_count: 1})
  await check('POST', `/post/${pid1}/mark`, {token: token2, is_mark: 1}, {mark_count: 2})

  // Upvote and comment counts
  await check('GET', `/post/${pid1}`, undefined, {
    author: {nickname: 'kayuyuko', avatar: ''},
    timestamp: any,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    tags: ['tag1', 'tag2'],
    upvote_count: 1,
    comment_count: 4,
    mark_count: 2,
  })

  // More posts for collections
  let pids = Array(5);
  for (let i = 0; i < pids.length; i++)
    pids[i] = (await check('POST', '/post/new', {
      token: token2,
      type: 0,
      caption: `Caption ${i}`,
      contents: `Lorem ipsum ${i}`,
      tags: `tag${i},tag${i*2+2}`,
      publish: 1,
    }, {id: any})).id

  // Collections
  let lid1 = (await check('POST', '/collection/new', {
    token: token2,
    title: 'Collection',
    description: 'A collection',
    tags: 'tag2,tag3,tag4',
  }, {id: any})).id
  await check('GET', `/collection/${lid1}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: ''},
    title: 'Collection',
    description: 'A collection',
    posts: [],
    tags: ['tag2', 'tag3', 'tag4'],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `+${pids[0]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 0', contents: 'Lorem ipsum 0'},
    ],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `+${pids[2]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 0', contents: 'Lorem ipsum 0'},
      {caption: 'Caption 2', contents: 'Lorem ipsum 2'},
    ],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `+${pids[1]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 0', contents: 'Lorem ipsum 0'},
      {caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {caption: 'Caption 1', contents: 'Lorem ipsum 1'},
    ],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `-${pids[0]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {caption: 'Caption 1', contents: 'Lorem ipsum 1'},
    ],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `+${pids[2]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {caption: 'Caption 1', contents: 'Lorem ipsum 1'},
    ],
  })
  await check('POST', `/collection/${lid1}/edit_posts`, {
    token: token2, op: `-${pids[3]}`
  }, {
    author: any, title: any, description: any, tags: any,
    posts: [
      {caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {caption: 'Caption 1', contents: 'Lorem ipsum 1'},
    ],
  })


  console.log(`\n${pass}/${total} passed`);
})();
