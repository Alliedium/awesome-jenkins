## Prerequisites
* [We used Jammy ubuntu VM ](https://github.com/Alliedium/awesome-proxmox). Use the [script](https://github.com/Alliedium/awesome-proxmox/blob/main/vm-cloud-init-shell/.env.example).  
* [Ansible is installed. Follow the second step](https://github.com/Alliedium/awesome-ansible#setting-up-config-machine))
* Install molecule on Ubuntu linux:
      ``` apt update
        apt install pip
        python3 -m pip install molecule ansible-core
        pip3 install 'molecule-plugins[docker]'```

## Playbook variables used in Jenkins server installation:
1. Jenkins host:
    `jenkins_hostname: 127.0.0.1`
2. Jenkins port: 
    `jenkins_port: 8085`
3.  Admin account credentials which will be created the first time Jenkins is installed: - check users in role
    `jenkins_admin_user: admin
     jenkins_admin_password: admin`
4. Java version:
    `java_packages:`
      `- openjdk-17-jdk`
5. Install global tools. Maven versions:
*     jenkins_maven_installations:
      - 3.8.4
      - 3.9.0
6. [List of plugins that will be installed](ListofJenkinsPluginsToBeInstalled.md)
o
# Instructions to install Jenkins with ansible playbook:
1. Install ansible roles for [Java](https://github.com/geerlingguy/ansible-role-java/), [Git](https://github.com/geerlingguy/ansible-role-git/), and [Jenkins](https://github.com/geerlingguy/ansible-role-jenkins) using commands:
    `ansible-galaxy install --roles-path=./playbooks geerlingguy.jenkins`
    `ansible-galaxy install --roles-path=./playbooks geerlingguy.java`
2. Run ansible playbook 
   1. Run all tasks from playbook.yml:
      `ansible-playbook ./playbooks/create-job.yml -i ./inventory`
   2. Run without creating any pipeline:
      `ansible-playbook ./playbooks/create-job.yml -i ./inventory --skip-tags never`
   3. Create only simple pipeline:
      `ansible-playbook ./playbooks/create-job.yml -i ./inventory --tags[always,pipeline]`
   4. See the list of all tasks:
      `ansible-playbook ./playbooks/create-job.yml -i ./inventory --skip-tags never --list-tags`
3. Go to the browser and check that Jenkins is available at http://localhost:8085/.
4. Login to Jenkins using the credentials.
5. You will see Jenkins dashboard. Open job. ![jenkins_dashboard.png](./images/01jenkins_dashboard.png) 
6. The main branch will be run for the single pipeline job ![single_pipeline.png](./images/02jenkins_pipeline.png)
7. The pull-requests will be run for the multibranch pipeline job.![multibranch_pipeline.png](./images/03jenkins_mpipeline.png)

### Project:
   As the example we used the following [project](https://github.com/Alliedium/springboot-api-rest-example)

### Job configuration:
   Job configuration is set in the templates/job-config.xml.j2.