FXEMOJI_DIR=~/Downloads/fxemoji-gh-pages

rm -rf fxemoji
mkdir fxemoji
# for n in redapple pineapple pear tangerine peach roastedsweetpotato fourleafclover doughnut droplet chocolatebar cookie cat candy shortcake ribbon; do
#   for subdir in objects nature; do
#     for ff in `find $FXEMOJI_DIR/svgs/$subdir -name "*$n.*"`; do
#       f=`echo $ff | sed -e 's/.*\///g' | sed -e 's/.svg//g'`
#       inkscape $ff --export-png=`pwd`/fxemoji/$f.png --export-dpi=24 -b "#fcfcfc"
#     done
#   done
# done
for ff in $FXEMOJI_DIR/svgs/objects/*-*; do
  f=`echo $ff | sed -e 's/.*\///g' | sed -e 's/.svg//g'`
  inkscape $ff --export-png=`pwd`/fxemoji/$f.png --export-dpi=24 -b "#fcfcfc"
done
