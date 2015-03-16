##Setting up the new block storage

Create the partition table and partion the drive

	$ fdisk /dev/xvde
		o #create a new empty DOS partition table
		n #add a new partition. 
		p  #Select 'p' for primary partition
 			1	#Select 1 for partition number
			 	 #Hit enter for the next two options for selecting sectors.
		q #Exit fdis


View the new partition
	
    $ fdisk -l #This will now list the new partion /dev/xde1
    
Format the partition

	$ mkfs.ext4 /dev/xvde1 #This will format the partiotion as ext4 file system


Create the new mount point directory

	mkdir /opt/dev

Add the following entry in /etc/fstab so that the drive is automounted on reboot
	
    /dev/xvde1 /opt/data           ext4    defaults        0       2
    

Mount the devices 

	mount -a
	
The drive is now available at `/opt/data`
	
