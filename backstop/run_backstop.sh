if [[ -z "$1" ]] ; then 
    echo "Parameter env is empty" ; 
    exit 1;
fi;
if [[ -z "$2" ]] ; then 
    echo "Parameter username is empty" ; 
    exit 1;
fi;
if [[ -z "$3" ]] ; then 
    echo "Parameter password is empty" ; 
    exit 1;
fi;
if [[ -z "$4" ]] ; then 
    echo "Parameter mode is empty" ; 
    exit 1;
fi;

cp -rf backstop.$1.json backstop.json;

cp onBefore.js onBefore_temp.js

file='onBefore_temp.js';
sed -i "s/__username__/$2/" $file
sed -i "s/__password__/$3/" $file

cp -rf onBefore_temp.js backstop_data/engine_scripts/puppet/onBefore.js;
rm onBefore_temp.js;

npm run-script $4;
