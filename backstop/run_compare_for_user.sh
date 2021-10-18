if [[ -z "$1" ]] ; then 
    echo "Parameter refEnv is empty" ; 
    exit 1;
fi;
if [[ -z "$2" ]] ; then 
    echo "Parameter testEnv is empty" ; 
    exit 1;
fi;
if [[ -z "$3" ]] ; then 
    echo "Parameter username is empty" ; 
    exit 1;
fi;
if [[ -z "$4" ]] ; then 
    echo "Parameter refPassword is empty" ; 
    exit 1;
fi;
if [[ -z "$5" ]] ; then 
    echo "Parameter testPassword is empty" ; 
    exit 1;
fi;
if [[ -z "$6" ]] ; then 
    echo "Parameter dest is empty" ; 
    exit 1;
fi;
./run_backstop.sh $1 $3 $4 reference;
./run_backstop.sh $2 $3 $5 test;
./save_reports.sh $1'_'$2'_'$3'_'$6;


