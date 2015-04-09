sudo apt-get install git
git clone  https://github.com/nikhilgk/halo-ml.git
sudo apt-get install build-essential python-dev python-setuptools \
                     python-numpy python-scipy \
                     libatlas-dev libatlas3gf-base
sudo apt-get install python-matplotlib
sudo apt-get install python-pip
sudo pip install --user --install-option="--prefix=" -U scikit-learn
sudo pip install pandas
