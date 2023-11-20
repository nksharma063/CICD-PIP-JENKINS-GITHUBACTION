pipeline { 
  agent any 
  environment {
      // Define the EC2 host and remote directory
      EC2_HOST = 'ubuntu@IPAddress' 
      REMOTE_DIR = '/home/ubuntu' 
  }

  stages {
    stage('Cloning the git repo') {
        steps {
            // Cloning the GitHub repository into the Jenkins workspace
            git url: 'https://github.com/nksharma063/CICD-PIP-JENKINS-GITHUBACTION.git', branch: 'main'
        }
    }
    stage('Copying web application to EC2 running with jenkins'){
        steps{
            echo 'Deploying into the ec2'
            sh "echo 'workspace directory is ${WORKSPACE}'"
            sh "ls ${WORKSPACE}"
            sh "pwd"
            sshagent(['aws-ec2-cred']){
                // Copy the workspace contents to the EC2 host
                sh "scp -o StrictHostKeyChecking=no ${WORKSPACE}/* ${EC2_HOST}:${REMOTE_DIR}"
            }
        }
    }
    stage('Build') {
        steps {
            // Install dependencies using pip
            sshagent(['aws-ec2-cred']){
                sh 'sshpass -p "your-password" sudo apt install -y python3-pip'
                sh 'sshpass -p "your-password" sudo pip install streamlit'
                sh 'sshpass -p "your-password" sudo pip install pytest'
            }
        }
    }
    stage('Test') {
        steps{
            // Run tests
            sh 'sshpass -p "your-password" sudo pytest /home/ubuntu/test_calculator.py'
        }
    }
    stage('SonarQube analysis') {
        steps {
            script {
                // Assuming you have SonarQube scanner installed and configured on your Jenkins server
                sh 'sonar-scanner'
            }
        }
    }
    stage('Deploy') {
        when {
            expression {
                // Check if the previous stage (Test) was successful
                def isTestSuccessful = currentBuild.resultIsBetterOrEqualTo('SUCCESS')
                // Check if the current workspace is as expected
                def isWorkspaceCorrect = (WORKSPACE == '/expected/workspace/path')
                // Check if the build is verified
                def isBuildVerified = currentBuild.displayName.contains('verified')
                // Return true only if all conditions are met
                return isTestSuccessful && isWorkspaceCorrect && isBuildVerified
            }
        }
        steps {
            // Send a success email after tests
            mail bcc: '', body: 'hello Testing is sucessfull', cc: '', from: '', replyTo: '', subject: 'email form jenkins', to: 'nksharma063@gmail.com'
            // Run the application
            sh 'sshpass -p "your-password" sudo streamlit run /home/ubuntu/calculator.py'
            // Transfer the converted code to the target machine
            sshagent(['aws-ec2-cred']){
                sh "scp -o StrictHostKeyChecking=no ${WORKSPACE}/calculator.py ${EC2_HOST}:${REMOTE_DIR}"
            }
            // Deploy under nginx server
            sshagent(['aws-ec2-cred']){
                sh "ssh -o StrictHostKeyChecking=no ${EC2_HOST} 'sudo cp ${REMOTE_DIR}/calculator.py /etc/nginx/sites-available/ && sudo ln -s /etc/nginx/sites-available/calculator.py /etc/nginx/sites-enabled/'"
                sh "ssh -o StrictHostKeyChecking=no ${EC2_HOST} 'sudo service nginx restart'"
            }
        }
    }
  }
  post{
    success{
        echo 'cloning successfully completed'
    }
    failure{
        echo 'Cloning failed'
    }
  }
}
