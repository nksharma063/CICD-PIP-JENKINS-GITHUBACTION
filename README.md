# CICD-PIP-JENKINS-GITHUBACTION
CICD pipeline using Jenkins and Github


In Jenkins, the term "workspace" is used to refer to the directory where Jenkins builds your project. By default, each job has its own workspace. 

The `WORKSPACE` environment variable in Jenkins contains the absolute path of this workspace directory. So, when you use `${WORKSPACE}` in your Jenkins pipeline script, it gets replaced by the path of the current job's workspace directory.

In your script, `${WORKSPACE}` is used to specify the location of the files that you want to copy to your EC2 instance. The line `sh "scp -o StrictHostKeyChecking=no ${WORKSPACE}/* ${EC2_HOST}:${REMOTE_DIR}"` is copying all files from the current job's workspace directory to the `REMOTE_DIR` directory on your EC2 instance.

This is useful because the workspace directory is where your project's files are located after the `git` step clones your repository. So, `${WORKSPACE}/*` represents all the files in your project.

Please note that the `scp` command requires SSH access to your EC2 instance. The `sshagent(['aws-ec2-cred'])` line in your script is providing these SSH credentials. You need to replace `'aws-ec2-cred'` with the ID of your actual SSH credentials in Jenkins. 

Also, please remember to handle all sensitive data securely, especially when dealing with remote servers and access credentials.
