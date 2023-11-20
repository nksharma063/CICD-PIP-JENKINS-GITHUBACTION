This script does the following:

Clones your GitHub repository into the Jenkins workspace.
Copies the contents of the workspace to your EC2 instance.
Installs the necessary dependencies using pip.
Runs your tests.
If the tests pass, it sends an email, runs your application, transfers the calculator.py file to the target machine, and deploys it under the nginx server.
Please replace 'ubuntu@IPAddress' with your actual EC2 instance’s IP address. Also, make sure that the Jenkins server has the necessary permissions to access your EC2 instance. You might need to add the Jenkins server’s public key to the authorized_keys file on your EC2 instance.

Remember to handle any sensitive information such as email addresses and IP addresses securely. It’s generally a good practice to use Jenkins credentials or environment variables to handle sensitive data.

This script assumes that you have sshagent, mail, and sshpass properly configured in your Jenkins server. If not, you might need to install and configure the necessary plugins and tools.

Please note that this is a basic script and might need to be adjusted based on your specific use case and environment. For example, the calculator.py file and its location might need to be adjusted based on your application. The nginx configuration might also need to be adjusted based on your server setup.

Also, please note that running commands with sudo might require password authentication, depending on your server’s sudoers configuration. If that’s the case, you might need to adjust the script to handle password prompts or configure your server to allow passwordless
