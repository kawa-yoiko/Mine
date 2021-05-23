const http = require('http');
const fs = require('fs');

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

const asyncUpload = (url, params) => new Promise((resolve, reject) => {
  const token = getToken(params);

  const fileContents = fs.readFileSync(params.file);

  const boundary = '----' +
    Math.random().toString().substr(2) + '-' +
    Math.random().toString().substr(2);
  const payloadHeader = Buffer.from(
    '--' + boundary + '\r\n' +
    'Content-Disposition: form-data; name="qwqwqwq"; filename="quququq"\r\n' +
    'Content-Type: application/octet-stream\r\n\r\n');
  const payloadFooter = Buffer.from('\r\n--' + boundary + '--\r\n');
  const payload = Buffer.concat([payloadHeader, fileContents, payloadFooter]);

  const opt = {
    method: 'POST',
    headers: {
      'Content-Type': `multipart/form-data; boundary=${boundary}`,
      'Content-Length': payload.length,
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
  req.write(payload);
  req.end();
});

const any = {};
const objEqual = (a, b) => {
  if (a === any || b === any) return true;
  if (typeof a !== typeof b) return false;
  if (typeof a !== 'object') return a === b;
  if ((a === null) ^ (b === null)) return false;

  if (b && !b._ignoreRedundant)
    for (let key in a) if (key[0] !== '_' && !objEqual(a[key], b[key])) return false;
  if (a && !a._ignoreRedundant)
    for (let key in b) if (key[0] !== '_' && !objEqual(a[key], b[key])) return false;
  return true;
};

let total = 0, pass = 0;

const check = async (method, url, params, expect, expect_status) => {
  const baseUrl = process.env['HOST'] || 'http://localhost:2317';
  const info = `${method + ' '.repeat(4 - method.length)} ` +
    `${url + ' '.repeat(25 - url.length)} | `;
  const [status, response, headers] =
    await (
      method === 'POST' ? asyncPost :
      method === 'PUT' ? asyncUpload : asyncGet
    )(baseUrl + url, params);

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

/*
  const bio1 = '我爱吃栗子';
  const bio2 = '我爱吃寿司';

  await check('POST', '/signup', {nickname: 'kayuyuko', email: 'kyyk@kawa.moe', password: 'P4$$w0rd'}, {error: 0}, 200)
  await check('POST', '/signup', {nickname: 'kurikoneko', email: 'kuriko@example.com', password: 'letme1n'}, {error: 0}, 200)
  await check('POST', '/login', {nickname: 'doesnotexist', password: '888888'}, undefined, 400)
  await check('POST', '/login', {nickname: 'kayuyuko', password: '888888'}, undefined, 400)
  let u1 = await check('POST', '/login', {nickname: 'kayuyuko', password: 'P4$$w0rd'}, {
    token: any,
    user: {nickname: 'kayuyuko', avatar: '', signature: '', collections: [any]}
  }, 200)
  let token1 = u1.token
  await check('GET', '/whoami', {token: '123123'}, undefined, 401)
  await check('GET', '/whoami', {token: token1},
    {nickname: 'kayuyuko', avatar: '', signature: '', collections: [any]})
  let u2 = await check('POST', '/login', {nickname: 'kurikoneko', password: 'letme1n'}, {
    token: any,
    user: {nickname: 'kurikoneko', avatar: '', signature: '', collections: [any]}
  }, 200)
  let token2 = u2.token

  // User modification
  await check('POST', '/whoami/edit',
    {token: token2, signature: bio2},
    {nickname: 'kurikoneko', avatar: '', signature: bio2, collections: [any]})
  await check('GET', '/whoami', {token: token2},
    {nickname: 'kurikoneko', avatar: '', signature: bio2, collections: [any]})
  await check('POST', '/whoami/edit',
    {token: token1, signature: bio1},
    {nickname: 'kayuyuko', avatar: '', signature: bio1, collections: [any]})

  // Avatar
  let avt1 = (await check('PUT', '/upload/avatar',
    {token: token1, file: 'avt1.png'},
    {nickname: 'kayuyuko', avatar: any, signature: bio1, collections: [any]})).avatar
  let avt2 = (await check('PUT', '/upload/avatar',
    {token: token2, file: 'avt2.png'},
    {nickname: 'kurikoneko', avatar: any, signature: bio2, collections: [any]})).avatar

  let u2img1 = (await check('PUT', '/upload',
    {token: token2, file: 'post1.png'},
    {ids: [any]})).ids[0]
  let u2img2 = (await check('PUT', '/upload',
    {token: token2, file: 'avt2.png'},
    {ids: [any]})).ids[0]

  // Posts
  let lid1 = u1.user.collections[0].id
  let lid2 = u2.user.collections[0].id
  await check('POST', '/post/new', {
    token: token2,
    type: 1,
    caption: '今天是甜粥粥。',
    contents: `${u2img1} ${u2img2}`,
    collection: lid2,
    tags: '美食,狗粮,每周粥粥',
  }, {id: any})
  let pid1 = (await check('POST', '/post/new', {
    token: token1,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    collection: lid1,
    tags: 'tag1,tag2',
  }, {id: any})).id
  let no_pid = pid1 + 10;
  await check('GET', `/post/${pid1}`, undefined, {
    author: {nickname: 'kayuyuko', avatar: avt1},
    timestamp: any,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    collection: {id: lid1, title: any},
    tags: ['tag1', 'tag2'],
    upvote_count: 0,
    comment_count: 0,
    star_count: 0,
  })
  await check('GET', `/post/${no_pid}`, undefined, undefined, 404)

  await check('POST', '/post/new', {
    token: token1,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    collection: lid2, // Others' collection
    tags: 'tag1,tag2',
  }, undefined, 403)

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
    {id: cid4, author: {nickname: 'kayuyuko', avatar: avt1}, timestamp: any, reply_user: null, contents: 'Another yes comment'},
    {id: cid1, author: {nickname: 'kurikoneko', avatar: avt2}, timestamp: any, reply_user: null, contents: 'No comment'},
  ])
  await check('GET', `/post/${pid1}/comments`, {
    token: token1,
    start: 0,
    count: 10,
    reply_root: cid1,
  }, [
    {id: cid3, author: {nickname: 'kurikoneko', avatar: avt2}, timestamp: any, reply_user: {nickname: 'kayuyuko', avatar: avt1}, contents: 'Unknown comment'},
    {id: cid2, author: {nickname: 'kayuyuko', avatar: avt1}, timestamp: any, reply_user: {nickname: 'kurikoneko', avatar: avt2}, contents: 'Yes comment'},
  ])
  await check('GET', `/post/${pid1}/comments`, {
    token: token1,
    start: 1,
    count: 1,
    reply_root: cid1,
  }, [
    {id: cid2, author: {nickname: 'kayuyuko', avatar: avt1}, timestamp: any, reply_user: {nickname: 'kurikoneko', avatar: avt2}, contents: 'Yes comment'},
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
  await check('POST', `/post/${pid1}/star`, {token: token1, is_star: 0}, {star_count: 0})
  await check('POST', `/post/${pid1}/star`, {token: token1, is_star: 1}, {star_count: 1})
  await check('POST', `/post/${pid1}/star`, {token: token2, is_star: 1}, {star_count: 2})
  await check('POST', `/post/${pid1}/star`, {token: token2, is_star: 1}, {star_count: 2})
  await check('POST', `/post/${pid1}/star`, {token: token2, is_star: 0}, {star_count: 1})
  await check('POST', `/post/${pid1}/star`, {token: token2, is_star: 1}, {star_count: 2})

  // Upvote and comment counts
  await check('GET', `/post/${pid1}`, undefined, {
    author: {nickname: 'kayuyuko', avatar: avt1},
    timestamp: any,
    type: 0,
    caption: 'Caption',
    contents: 'Lorem ipsum',
    collection: {id: lid1, title: any},
    tags: ['tag1', 'tag2'],
    upvote_count: 1,
    comment_count: 4,
    star_count: 2,
  })

  // More posts for collections
  let pids = Array(5);
  for (let i = 0; i < pids.length; i++)
    pids[i] = (await check('POST', '/post/new', {
      token: token2,
      type: 0,
      caption: `Caption ${i}`,
      contents: `Lorem ipsum ${i}`,
      collection: lid2,
      tags: `tag${i},tag${i*2+2}`,
    }, {id: any})).id

  // Collections
  await check('GET', `/collection/${lid1}`, undefined, {
    author: {nickname: 'kayuyuko', avatar: avt1},
    title: any,
    description: any,
    posts: [
      {id: pid1, type: 0, caption: 'Caption', contents: 'Lorem ipsum'}
    ],
    tags: [],
    subscription_count: 0,
  })
  await check('GET', `/collection/${lid2}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: avt2},
    title: any,
    description: any,
    posts: [
      {id: any, type: 0, caption: '今天是甜粥粥。', contents: any},
      {id: pids[0], type: 0, caption: 'Caption 0', contents: 'Lorem ipsum 0'},
      {id: pids[1], type: 0, caption: 'Caption 1', contents: 'Lorem ipsum 1'},
      {id: pids[2], type: 0, caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {id: pids[3], type: 0, caption: 'Caption 3', contents: 'Lorem ipsum 3'},
      {id: pids[4], type: 0, caption: 'Caption 4', contents: 'Lorem ipsum 4'},
    ],
    tags: [],
    subscription_count: 0,
  })
  let lid3 = (await check('POST', '/collection/new', {
    token: token2,
    title: 'Collection',
    description: 'A collection',
    tags: 'tag2,tag3,tag4',
  }, {id: any})).id
  await check('GET', `/collection/${lid3}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: avt2},
    title: 'Collection',
    description: 'A collection',
    posts: [],
    tags: ['tag2', 'tag3', 'tag4'],
    subscription_count: 0,
  })

  await check('POST', `/post/${pids[1]}/set_collection`, {
    token: token2,
    collection_id: lid3,
  }, {})
  await check('POST', `/post/${pids[3]}/set_collection`, {
    token: token2,
    collection_id: lid3,
  }, {})
  await check('GET', `/collection/${lid2}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: avt2},
    title: any,
    description: any,
    posts: [
      {id: any, type: 0, caption: '今天是甜粥粥。', contents: any},
      {id: pids[0], type: 0, caption: 'Caption 0', contents: 'Lorem ipsum 0'},
      {id: pids[2], type: 0, caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {id: pids[4], type: 0, caption: 'Caption 4', contents: 'Lorem ipsum 4'},
    ],
    tags: [],
    subscription_count: 0,
  })
  await check('GET', `/collection/${lid3}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: avt2},
    title: 'Collection',
    description: 'A collection',
    posts: [
      {id: pids[1], type: 0, caption: 'Caption 1', contents: 'Lorem ipsum 1'},
      {id: pids[3], type: 0, caption: 'Caption 3', contents: 'Lorem ipsum 3'},
    ],
    tags: ['tag2', 'tag3', 'tag4'],
    subscription_count: 0,
  })

  await check('POST', `/post/${pids[0]}/set_collection`, {
    token: token2,
    collection_id: lid1,  // Others' collection
  }, undefined, 403)
  await check('POST', `/post/${pids[0]}/set_collection`, {
    token: token1,  // Others' post
    collection_id: lid1,
  }, undefined, 403)
  await check('POST', `/post/${no_pid}/set_collection`, {
    token: token1,
    collection_id: lid1,
  }, undefined, 404)

  // Subscription
  await check('POST', `/collection/${lid2}/subscribe`, {token: token1, is_subscribe: 1}, {subscription_count: 1})
  await check('POST', `/collection/${lid2}/subscribe`, {token: token1, is_subscribe: 1}, {subscription_count: 1})
  await check('POST', `/collection/${lid2}/subscribe`, {token: token2, is_subscribe: 0}, {subscription_count: 1})
  await check('POST', `/collection/${lid2}/subscribe`, {token: token2, is_subscribe: 1}, {subscription_count: 2})
  await check('POST', `/collection/${lid2}/subscribe`, {token: token1, is_subscribe: 0}, {subscription_count: 1})
  await check('GET', `/collection/${lid2}`, undefined, {
    author: {nickname: 'kurikoneko', avatar: avt2},
    title: any,
    description: any,
    posts: [
      {id: any, type: 0, caption: '今天是甜粥粥。', contents: any},
      {id: pids[0], type: 0, caption: 'Caption 0', contents: 'Lorem ipsum 0'},
      {id: pids[2], type: 0, caption: 'Caption 2', contents: 'Lorem ipsum 2'},
      {id: pids[4], type: 0, caption: 'Caption 4', contents: 'Lorem ipsum 4'},
    ],
    tags: [],
    subscription_count: 1,
  })
  await check('GET', `/subscription_timeline`, {token: token2, start: 0, count: 10}, [any, any, any, any])
  await check('GET', `/subscription_timeline`, {token: token1, start: 0, count: 10}, [])

  await check('POST', `/collection/${lid3}/subscribe`, {token: token2, is_subscribe: 1}, {subscription_count: 1})
  await check('GET', `/subscription_timeline`, {token: token2, start: 0, count: 10}, [any, any, any, any, any, any])
  await check('POST', `/collection/${lid2}/subscribe`, {token: token2, is_subscribe: 0}, {subscription_count: 0})
  await check('GET', `/subscription_timeline`, {token: token2, start: 0, count: 10}, [any, any])

  // Truncation in timeline
  let lid4 = (await check('POST', '/collection/new', {
    token: token1,
    title: 'Truncation test',
    description: 'Subscribe!',
    tags: 'a,b,c',
  }, {id: any})).id
  await check('POST', '/post/new', {
    token: token1,
    type: 0,
    caption: 'Very long text',
    contents: '哈'.repeat(5000),
    collection: lid4,
    tags: 'd,e,f',
  }, {id: any})
  await check('POST', '/post/new', {
    token: token1,
    type: 1,
    caption: 'Multiple images',
    contents: `${u2img1} ${u2img2} ${u2img2}`,
    collection: lid4,
    tags: 'd,e,f',
  }, {id: any})
  await check('POST', `/collection/${lid4}/subscribe`, {token: token1, is_subscribe: 1}, {subscription_count: 1})
  await check('GET', `/subscription_timeline`, {token: token1, start: 0, count: 10}, [
    {_ignoreRedundant: true, contents: '哈'.repeat(300)},
    {_ignoreRedundant: true, contents: `${u2img1}`},
  ])
*/

  // Sample dataset!
  const rand = (() => {
    let seed = 523;
    return () => {
      // https://stackoverflow.com/a/3428186
      // Robert Jenkins' 32 bit integer hash function.
      seed = ((seed + 0x7ed55d16) + (seed << 12))  & 0xffffffff;
      seed = ((seed ^ 0xc761c23c) ^ (seed >>> 19)) & 0xffffffff;
      seed = ((seed + 0x165667b1) + (seed << 5))   & 0xffffffff;
      seed = ((seed + 0xd3a2646c) ^ (seed << 9))   & 0xffffffff;
      seed = ((seed + 0xfd7046c5) + (seed << 3))   & 0xffffffff;
      seed = ((seed ^ 0xb55a4f09) ^ (seed >>> 16)) & 0xffffffff;
      return seed & 0xfffffff;
    };
  })();
  const RAND_MAX = 0x10000000;

  const N = 10;   // Number of users
  const C = 3;    // Number of collections per user (minimum)
  const Cd = 5;   // (variation)
  const M = 10;   // Average number of posts per collection
  const S = 50;   // Number of stars per user (minimum)
  const Sd = 150; // (variation)
  const T = 100;  // Number of comments per user (minimum)
  const Td = 500; // (variation)
  const B = 5;    // Number of subscriptions per user (minimum)
  const Bd = 15;  // (variation)

  const token = Array(N);
  for (let i = 0; i < N; i++) {
    await check('POST', '/signup',
      {nickname: `uu${i}`, email: `uu${i}@example.com`, password: `aaaaaa${i}`}, any);
    token[i] = (await check('POST', '/login',
      {nickname: `uu${i}`, password: `aaaaaa${i}`}, any)).token;
  }

  // Upload images
  const images = Array.from(Array(N), () => []);
  const dir = await require('fs/promises').readdir('fxemoji');
  for (const file of dir) {
    const u = rand() % N;
    const id = (await check('PUT', '/upload',
      {token: token[u], file: `fxemoji/${file}`},
      {ids: [any]})).ids[0];
    images[u].push(id);
  }

  // Create collections
  const colls = Array.from(Array(N), () => []);
  const collsAll = [];
  for (let u = 0; u < N; u++) {
    let cn = C + rand() % Cd;
    for (let c = 0; c < cn; c++) {
      const id = (await check('POST', '/collection/new', {
        token: token[u],
        title: `Collection ${u}-${c}`,
        description: `A collection ${u}-${c}`,
        tags: 'tag1,tag2',
      }, any)).id;
      colls[u].push(id);
      collsAll.push(id);
    }
  }

  const shuffleRepeated = (N, f) => {
    const a = [];
    for (let u = 0; u < N; u++)
      for (let i = f(u); i > 0; i--) {
        const j = rand() % (a.length + 1);
        let v = u;
        if (j !== a.length) {
          v = a[j];
          a[j] = u;
        }
        a.push(v);
      }
    return a;
  };

  // Create posts
  const postsAll = [];
  for (let u of shuffleRepeated(N, (u) => M * colls[u].length + rand() % 10)) {
    const contentImages = Array.from(Array(rand() % 9),
      () => images[u][rand() % images[u].length]);
    const id = (await check('POST', '/post/new', {
      token: token[u],
      type: 1,
      caption: '今天是甜粥粥。',
      contents: contentImages.join(' '),
      collection: colls[u][rand() % colls[u].length],
      tags: 'tag3,tag4',
    }, {id: any})).id;
    postsAll.push(id);
  }

  // Stars
  for (let u = 0; u < N; u++)
    for (let s = S + rand() % Sd; s > 0; s--) {
      await check('POST', `/post/${postsAll[rand() % postsAll.length]}/star`,
        {token: token[u], is_star: 1}, any);
    }

  // Comments
  const postCmts = {};
  for (let u of shuffleRepeated(N, (u) => T + rand() % Td)) {
    const post = postsAll[rand() % postsAll.length];

    let cmts = postCmts[post];
    if (cmts === undefined) postCmts[post] = cmts = [];

    const cid = (await check('POST', `/post/${post}/comment/new`, {
      token: token[u],
      reply_to: (cmts.length === 0 || rand() % 2 === 0) ? -1 : cmts[rand() % cmts.length],
      contents: 'No comment',
    }, {id: any})).id;
    cmts.push(cid);
  }

  // Subscriptions
  for (let u = 0; u < N; u++)
    for (let b = B + rand() % Bd; b > 0; b--) {
      await check('POST', `/collection/${collsAll[rand() % collsAll.length]}/subscribe`,
        {token: token[u], is_subscribe: 1}, any);
    }

  console.log(token);

  console.log(`\n${pass}/${total} passed`);
})();
