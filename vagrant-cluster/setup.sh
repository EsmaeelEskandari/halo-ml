wget https://dl.bintray.com/mitchellh/vagrant/vagrant_1.7.2_x86_64.deb -O /tmp/vagrant.deb
dpkg -i /tmp/vagrant.deb
apt-get -y install software-properties-common
apt-add-repository ppa:ansible/ansible -y 
apt-get update
apt-get -y install ruby ruby1.9.1-dev make ansible tmux
vagrant plugin install vagrant-softlayer
