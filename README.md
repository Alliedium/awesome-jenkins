## This ansible playbook installs [`Jenkins`](https://www.jenkins.io/doc/) on specified host ##

# Prerequisites
* Run the ansible playbook on `Debian` or `Ubuntu`. [Used was VM with Jammy Ubuntu](https://github.com/Alliedium/awesome-proxmox). Use the [script](https://github.com/Alliedium/awesome-proxmox/blob/main/vm-cloud-init-shell/.env.example) to create VM on `Proxmox`.  

* Use `$HOME/awesome-jenkins/inventory/localhost/hosts.yaml` if you are installing the `Jenkins` on the same host where `Ansible` is running.  Use `$HOME/awesome-jenkins/inventory/example/hosts.yaml` if you are installing the `Jenkins` on the remote host.
  
  In our examples, we use `$HOME/awesome-jenkins/inventory/localhost/hosts.yaml` file.

* Install Ansible: [Follow the second step](https://github.com/Alliedium/awesome-ansible#setting-up-config-machine)

* [Install `molecule`](https://molecule.readthedocs.io/installation/) on `Ubuntu` Linux. Molecule project is designed to aid in the development and testing of Ansible roles.
  
   ```
   apt update
   apt install pip
   python3 -m pip install molecule ansible-core
   pip3 install 'molecule-plugins[docker]'
   ```

## Playbook variables used in Jenkins server installation:

1. The HTTP port for `Jenkins` web interface:

   ```
   jenkins_http_port: 8085
   ```

2. Admin account credentials which will be created the first time `Jenkins` is installed:

   ```
   jenkins_admin_username: admin
   jenkins_admin_password: admin
   ```

3. Java version:
   
   ```   
   java_packages: 
     - openjdk-17-jdk
   ```

4. Install global tools. Maven versions:
    
   ```
   jenkins_maven_installations:
     - 3.8.4
     - 3.9.0
   ```

5. [List of plugins that will be installed](ListofJenkinsPluginsToBeInstalled.md)

6. Multibranch pipeline job's repository url. Please change this parameter to the url of your fork:

   ```
   multibranch_repository_url: "https://github.com/Alliedium-demo-test/springboot-api-rest-example.git"
   ```


## Instructions to install Jenkins with ansible playbook

### 1. Clone repo:

  ```
  git clone https://github.com/Alliedium/awesome-jenkins.git $HOME/awesome-jenkins
  ```
### 2. Installing `Jenkins` on remote host

* Copy `$HOME/awesome-jenkins/inventory/example` to `$HOME/awesome-jenkins/inventory/my-jenkins` folder.
  
  ```
  cp -r $HOME/awesome-jenkins/inventory/example $HOME/awesome-jenkins/inventory/my-jenkins
  ```

* Change the variables in the files `$HOME/awesome-jenkins/inventory/my-jenkins/hosts.yml` as you need

![](./images/hosts.png)

* Installing `Jenkins` on localhost does not require any changes to `$HOME/awesome-jenkins/inventory/localhost/hosts.yml` file.

### 3. Install ansible roles for [Java](https://github.com/geerlingguy/ansible-role-java/), [Git](https://github.com/geerlingguy/ansible-role-git/), and [Jenkins](https://github.com/geerlingguy/ansible-role-jenkins) using commands:
   
   ```
   ansible-galaxy install -r $HOME/awesome-jenkins/requirements.yml
   ```

### 4. Run ansible playbook 

  This playbook contains multiple tasks that install `git`, `java`, `Jenkins`, as well as plugins, tools and pipelines in `Jenkins`. Using `Ansible` tags you can run a part of tasks. In our playbook we use 7 tags: `always`, `step1`, `step2`, `step3`, `step4`, `step5` and `step6`. Use `-t <tag_name>` flag to specify desired tag. They form a hierarchy of tags from `always` to `step6`. In this hierarchy, each subsequent tag includes both the tasks marked by this tag as well as tasks relating to all preceding tags, e.g. if you run playbook with `step3` tag, tasks tagged with `always`, `step1`, `step2` and `step3` will be run.

   1. Before running tasks, check the list of tasks that will be executed using `--list-tasks` flag
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost --list-tasks
   ```

   You will receive a list of all tasks. Using `-t step2` when getting a list of tasks.

   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step2 --list-tasks
   ```

   You will receive a list of tasks, tagged `always`, `step1` and `step2`.


   2. Run all the available tasks from `playbook.yml` playbook. 
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost
   ```
   3. Run without installing any plugins in `Jenkins`:
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step1
   ```

   4. Run with installing [plugins](ListofJenkinsPluginsToBeInstalled.md) in `Jenkins`:
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step2
   ```

   5. Use `step3` tag - install `python-jenkins`
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step3
   ```

   6. `step4` - Add  `maven` tool
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step4
   ```

   7. `step5` - Create and launch  `Jenkins pipeline job`
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step5
   ```
   
   8. `step6 - Create and launch` Jenkins multibranch pipeline job`
   
   ```
   ansible-playbook $HOME/awesome-jenkins/playbooks/create-job.yml -i $HOME/awesome-jenkins/inventory/localhost -t step6
   ```

### 5. Checkup `Jenkins`

1. Go to the host specified in the `$HOME/awesome-jenkins/inventory/localhost/hosts.yml` file, open browser and check that `Jenkins` is available at http://localhost:8085/.
2. Login to `Jenkins` using the credentials.
3. You will see `Jenkins` dashboard. Open job. ![jenkins_dashboard.png](./images/01jenkins_dashboard.png) 
4. The main branch will be run for the single pipeline job ![single_pipeline.png](./images/02jenkins_pipeline.png)
5. Pull requests will be run for the multibranch pipeline job.![multibranch_pipeline.png](./images/03jenkins_mpipeline.png)

### 5. Ansible playbook local testing with [molecule](https://molecule.readthedocs.io/)

The `molecule` configuration files are located in the `$HOME/awesome-jenkins/molecule/default` folder.

`molecule.yml` - this is the core file for Molecule. Used to define your testing steps, scenarios, dependencies, and other configuration options.

`converge.yml` - this is the playbook that Molecule will run to provision the targets for testing.

`verify.yml` - this is the playbook that is used to validate that the already converged instance state matches the desired state. 

Before running the `molecule` command, go to `awesome-jenkins` project

```
cd $HOME/awesome-jenkins
```

* Run Ansible playbook test after which all previously created resources are deleted.
  
```
molecule test
```

The `test` command will run the entire scenario; creating, converging, verifying.

* Ansible playbook execution or role in target infrastructure, without testing. In this case, molecule will run the Ansible playbook in docker
  
```
molecule converge
```

* Run Ansible playbook test after the infrastructure has been converged using the "molecule converge" command. All previously created resources are not deleted
  
```
molecule verify
```

* Navigate to the target infrastructure - the docker container with the debug or check target

```
molecule login
```

* Reset molecule temporary folders.

```
molecule reset
```

* Finally, to clean up, we can run

```
molecule destroy
```

This removes the containers that we deployed and provisioned with create or converge. Putting us into a great place to start again.

### 6. Ansible playbook remote testing with GitHub Actions

The `$HOME/awesome-jenkins/.github/workflows/ci.yml` file describes the steps for `GitHub` Actions testing.

After creating or updating a pull request, tests are launched on the `GitHub` server and the results can be viewed here

![github_actions](./images/github_actions.png)

![github_actions_1](./images/github_actions_1.png)

## `Jenkins` and `GitHub` integration

### 1. Set Resource Root URL

![resource_root_url](./images/resource_root_url.png)

### 2. Creating your organization in `GitHub`
  
  ![creating_org_1](./images/creating_org_1.png)

  ![creating_org_2](./images/creating_org_2.png)

### 3. Creating `GitHub apps`

![github_app](./images/github_app.png)

### 4. Generate and download SSH key

![](./images/ssh_key.png)

### 5. Install your app for repositories

![install_app](./images/install_app.png)

### 6. Convert your generated key

```
openssl pkcs8 -topk8 -inform PEM -outform PEM -in key-in-your-downloads-folder.pem -out converted-github-app.pem -nocrypt
```

`key-in-your-downloads-folder.pem` - your generated SSH key

`converted-github-app.pem` - converted key

### 7. Fork your repo for testing purposes on `GitHub`

  ![fork](./images/fork.png)

### 8. Create `multibranch pipeline` in `Jenkins`

![mpipeline](./images/mpipeline.png)

![mp_config](./images/mp_config_3.png)

### 9. On `GitHub` create new branch and pull request

After creating new pull request on `Jenkins` scan repository

![scan_repository](./images/scan_repository.png)

### 10. Run your build

![run_pr](./images/run_pr.png)

### 11. See build result on `GitHub`

![github_checks](./images/github_checks.png)

## Project:
   As the example we used the following [project](https://github.com/Alliedium/springboot-api-rest-example)

### Job configuration:
   Job configuration is set in the templates/job-config.xml.j2 - pipeline config and templates/multibranch-pipeline-config.xml.j2

## GitHub Actions

### Get familiar with GitHub workflows
1. Get familiar with GitHub actions functionality by following the examples from  [GitHub Actions examples](https://github.com/orgs/Alliedium-Awesome-GitHub-Actions/repositories)
2. Fork the repositories to run examples with GitHub actions workflows

### Run GitHub Actions
1. Fork repository on GitHub. 
2. The pipeline workflow is described in the `ci.yaml` file in `.github/workflows/` repository.
3. Navigate to Actions and enable them if needed. ![enable_github_actions.png](./images/04gha_enable.png)
4. The existing workflows can be run manually by following steps marked with the numbers 1-4 from the Figure below or triggered by pull request, see marks 5-7. ![run_existing_gha_wfs.png](./images/05gha_run_existing_workflow.png)-

## Nektos Act
### Install Nektos Act on Ubuntu Jammy
   ```
   sudo apt install act
   ```
  To install Nektos Act on other OS follow the instructions from [section](https://github.com/nektos/act#installation-through-package-managers)


## References

#### Ansible roles used in playbook
1. [Ansible galaxy Java role](https://github.com/geerlingguy/ansible-role-java/)
2. [Ansible galaxy Git role](https://github.com/geerlingguy/ansible-role-git/)
3. [Ansible galaxy Jenkins role](https://github.com/geerlingguy/ansible-role-jenkins)

#### GitOps workflow
4. [DevOps guide: pipeline challenges latest trends](https://www.polestarllp.com/blog/devops-guide-pipeline-challenges-latest-trends)
5. [Gitflow workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
6. [GitOps principles](https://rafay.co/the-kubernetes-current/gitops-principles-and-workflows-every-team-should-know/)

#### Maven profiles
7. [Maven profiles documentation](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)
8. [Maven profiles guide](https://www.baeldung.com/maven-profiles)
9. [Maven profiles. Examples](https://medium.com/javarevisited/maven-profiles-detailed-explanation-1b4c8204466a)

#### CI/CD
10. [CI/CD basics](https://www.synopsys.com/glossary/what-is-cicd.html)
11. [CI/CD basics](https://www.redhat.com/en/topics/devops/what-is-ci-cd)
12. [CI/CD fundamentals](https://about.gitlab.com/topics/ci-cd/)

#### CI/CD Tools
13. [CI/CD tools review](https://testsigma.com/blog/ci-cd-tools/)
14. [Jenkins vs Jenkins X](https://www.educative.io/answers/what-is-the-difference-between-jenkins-and-jenkins-x)
15. [Jenkins user documentation](https://www.jenkins.io/doc/)
16. [Jenkins X](https://jenkins-x.io/)
17. [GitHub actions docs](https://docs.github.com/en/actions)
18. [Argo Workflows - The workflow engine for Kubernetes](https://argoproj.github.io/argo-workflows/)
19. [Tekton - Cloud Native CI/CD](https://tekton.dev/)
20. [GitLab CI/CD docs](https://docs.gitlab.com/ee/ci/)

#### Jenkins pipelines
21. [Jenkins pipelines](https://www.jenkins.io/doc/book/pipeline/getting-started/) 
22. [Jenkinsfile](https://www.jenkins.io/doc/book/pipeline/jenkinsfile/)
23. [Jenkins pipeline syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
24. [Pipeline stage view Jenkins plugin](https://plugins.jenkins.io/pipeline-stage-view/)
25. [Rendering User Content](https://www.jenkins.io/doc/book/security/user-content/)

#### Debugging Jenkins pipeline
26. [Jenkins script console](https://www.jenkins.io/doc/book/managing/script-console/)
27. [Debugging Jenkins pipeline](https://notes.asaleh.net/posts/debugging-jenkins-pipeline/)
28. [Debugging tips](https://dzone.com/articles/10-tips-to-debug-jenkins-pipelines)
29. [Testing framework for Jenkins pipelines](https://github.com/jenkinsci/JenkinsPipelineUnit)

#### Static code analysis
30. [Spotbugs maven plugin](https://spotbugs.github.io/spotbugs-maven-plugin/)
31. [PMD/CPD static analysis plugin](https://pmd.github.io/latest/pmd_userdocs_tools_maven.html)
32. [Checkstyle plugin](https://checkstyle.org/)
33. [Code coverage tools](https://www.softwaretestinghelp.com/code-coverage-tools/style)
34. [Comparison of findbugs, pmd and checkstyle](https://www.sw-engineering-candies.com/blog-1/comparison-of-findbugs-pmd-and-checkstyle)
35. [Spotbugs docs](https://spotbugs.readthedocs.io/en/stable/introduction.html)
36. [Spotbugs maven plugin docs](https://spotbugs.readthedocs.io/en/stable/maven.html)
37. [Code style analysis reports publisher on Jenkins](https://plugins.jenkins.io/warnings-ng/)
38. [Code coverage tools](https://www.softwaretestinghelp.com/code-coverage-tools/)
39. [Jacoco documentation](https://www.eclemma.org/jacoco/trunk/doc/maven.html)[Jacoco examples](https://www.baeldung.com/jacoco)
40. [Jacoco examples](https://www.baeldung.com/jacoco)
41. [Code coverage reports publisher on Jenkins](https://plugins.jenkins.io/htmlpublisher/)

### Jenkins and GitHub integration
42. [GitHub checks Jenkins plugin](https://plugins.jenkins.io/github-checks/)
43. [Disable GitHub multibranch status Jenkins plugin](https://plugins.jenkins.io/disable-github-multibranch-status/)
44. [How to integrate Jenkins with GitHub](https://docs.cloudbees.com/docs/cloudbees-ci/latest/cloud-admin-guide/github-app-auth)
45. [How to create GitHub App](https://docs.github.com/en/apps/creating-github-apps/setting-up-a-github-app/creating-a-github-app)
46. [Manage protected branches on GitHub](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)
47. [Setup GitHub checks in Jenkinsfile](https://github.com/jenkinsci/checks-api-plugin/blob/master/docs/consumers-guide.md)

### GitHub Actions
48. [GitHub Actions workflows](https://docs.github.com/en/actions/using-workflows/about-workflows)
49. [GitHub Actions workflows basics, examples and a quick tutorial](https://codefresh.io/learn/github-actions/github-actions-workflows-basics-examples-and-a-quick-tutorial/)

### Act
50. [Act](https://github.com/nektos/act)
51. [GitHub Actions on your local machine](https://dev.to/ken_mwaura1/run-github-actions-on-your-local-machine-bdm)
52. [Debug GitHub Actions locally with act](https://everyday.codes/tutorials/debug-github-actions-locally-with-act/)