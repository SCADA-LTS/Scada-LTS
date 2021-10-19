cd backstop_data;
mkdir $1;
cp -arf html_report $1;
cp -arf bitmaps_reference $1;
cp -arf bitmaps_test $1;
rm -rf html_report;
rm -rf bitmaps_reference;
rm -rf bitmaps_test;
cd ..
