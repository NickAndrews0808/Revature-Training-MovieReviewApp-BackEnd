pipeline {
    agent any
    
    environment {
        S3_BUCKET = 'trng2309-7'
        SECRET_BUCKET = 'kyles-secret-bucket'
        AWS_REGION = 'us-east-2'
        DOCKER_IMAGE = 'spring-backend-07'
        EXTERNAL_PORT = '8087'
        INTERNAL_PORT = '8087'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/NickAndrews0808/Revature-Training-MovieReviewApp-BackEnd.git'
            }
        }

        stage('Fetch Secrets') {
            steps {
                sh 'aws s3 cp s3://${SECRET_BUCKET}/team7/application.properties movie-review-demo/src/main/resources/'
            }
        }
        
        stage('Build Spring Backend') {
            steps {
                dir('movie-review-demo') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Cleanup Old Docker Resources') {
            steps {
                sh '''
                    docker stop ${DOCKER_IMAGE} || true
                    docker rm ${DOCKER_IMAGE} || true
                    docker system prune -a -f
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
                sh 'docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest'
            }
        }
        
        stage('Deploy Backend Container') {
            steps {
                sh 'docker run -d --name ${DOCKER_IMAGE} -p ${EXTERNAL_PORT}:${INTERNAL_PORT} ${DOCKER_IMAGE}:latest'
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}