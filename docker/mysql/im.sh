#!/bin/bash
echo "mysql 0";
export DEBIAN_FRONTEND=noninteractive
echo "mysql 1";
MYSQL_ROOT_PASSWORD='root' # SET THIS! Avoid quotes/apostrophes in the password, but do use lowercase + uppercase + numbers + special chars
echo "mysql 2";
# Install MySQL
echo "mysql 3";
debconf-set-selections <<< "mysql-server-5.7 mysql-server/root_password password $MYSQL_ROOT_PASSWORD"
debconf-set-selections <<< "mysql-server-5.7 mysql-server/root_password_again password $MYSQL_ROOT_PASSWORD"
echo "mysql 4";
apt-get -qq install mysql-server > /dev/null # Install MySQL quietly
echo "mysql 5";
# Install Expect
apt-get -qq install expect > /dev/null
echo "mysql 6";
# Build Expect script
echo "mysql 7";
tee ~/secure_our_mysql.sh > /dev/null << EOF
spawn $(which mysql_secure_installation)
echo "mysql 8";
expect "Enter password for user root:"
send "$MYSQL_ROOT_PASSWORD\r"
echo "mysql 9";
expect "Press y|Y for Yes, any other key for No:"
send "y\r"
echo "mysql 10";
expect "Please enter 0 = LOW, 1 = MEDIUM and 2 = STRONG:"
send "2\r"
echo "mysql 11";
expect "Change the password for root ? ((Press y|Y for Yes, any other key for No) :"
send "n\r"
echo "mysql 12";
expect "Remove anonymous users? (Press y|Y for Yes, any other key for No) :"
send "y\r"
echo "mysql 13";
expect "Disallow root login remotely? (Press y|Y for Yes, any other key for No) :"
send "y\r"
echo "mysql 14";
expect "Remove test database and access to it? (Press y|Y for Yes, any other key for No) :"
send "y\r"
echo "mysql 15";
expect "Reload privilege tables now? (Press y|Y for Yes, any other key for No) :"
send "y\r"

EOF

# Run Expect script.
# This runs the "mysql_secure_installation" script which removes insecure defaults.
expect ~/secure_our_mysql.sh
echo "mysql 16";
# Cleanup
rm -v ~/secure_our_mysql.sh # Remove the generated Expect script
echo "mysql 17";
#sudo apt-get -qq purge expect > /dev/null # Uninstall Expect, commented out in case you need Expect

echo "MySQL setup completed. Insecure defaults are gone. Please remove this script manually when you are done with it (or at least remove the MySQL root password that you put inside it."
