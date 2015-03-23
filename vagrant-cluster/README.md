##Step 1 - Software installation

Create a master control instance where you will have vagrant and ansible installedd. This instance will be used to create, launch, manage and destroy your cluster. We will install Vagrant and Ansible on this machine. Alternatively, you could use your laptop or any other instance as your control instance, but then you will have to ensure rest of the installs from Step 1 are properly set up on your machine

Create a new Instance on Softlayer with

-	1 Core
-	1 GB RAM
-	Ubuntu 64 bit minimal install

Run

	cd
	apt-get -y install git
	git clone https://github.com/nikhilgk/halo-ml
	cd ~/halo-ml/vagrant-cluster/
	./setup.sh
	

##Step 2 - Setting up keys

Run

	ssh-keygen -f  ~/.ssh/halo-control -N ""
	less ~/.ssh/halo-control.pub

Copy the contents of the file and then go to https://control.softlayer.com/devices/sshkeys Click "Add" and paste the content to the "Key Contents" field and set the label to `halo_control` and click "Add". 
If you had already done this once, or if you want to reuse an existing key, then copy the private key to `~/.ssh/` and adjust the names in step 3 accordingly


## Step 3 - Configure the cluster

Now go to https://control.softlayer.com/account/user/profile and not your SoftLayer user name and Authentication/API Key. Back at the terminal,

	cd ~/halo-ml/vagrant-cluster/
	nano sl_config.yml

and update the following section

	# SoftLayer API credentials
	sl_username: "SL***"
	sl_api_key: "******************"
	sl_ssh_keys: "halo_control"
	sl_private_key_path: "/root/.ssh/halo-control.pub"

Review and edit rest of the file to define the cluster. Note that the total number of instances spun up will be one greater than `num_workers` since one instance will be a master node.

##Step 4 - Use the cluster

To create the cluster

	vagrant up

To connect to the master node

	vagrant ssh master

To destroy the cluster

	vagrant destroy






