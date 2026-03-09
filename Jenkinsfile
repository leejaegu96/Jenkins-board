pipeline {
    agent any

    stages {

        stage('Git Clone') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/leejaegu96/Jenkins-board.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew build --no-daemon'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t spring-app .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker stop spring-app || true'
                sh 'docker rm spring-app || true'
                sh 'docker run -d -p 8085:8085 --name spring-app spring-app'
            }
        }

    }
}