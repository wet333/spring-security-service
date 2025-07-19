pipeline {
	agent any

	environment {
		DOCKERHUB_USER = 'wetagustin'
		IMAGE_NAME = 'spring-security-service'
	}

	tools {
		maven 'Maven'
	}

	triggers {
		pollSCM('H/2 * * * *') // Poll every 5 minutes
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
				echo 'Checkout project.'
				echo "Build number: ${env.BUILD_NUMBER}"
				echo "Job name: ${env.JOB_NAME}"
			}
		}

		stage('Build') {
			steps {
				sh 'mvn clean compile'
			}
		}

		stage('Test') {
			steps {
				sh 'mvn test'
			}

			post {
				always {
					publishTestResults(testResultsPattern: 'target/surefire-reports/*.xml')
				}
			}
		}

		stage('Package') {
			steps {
				sh 'mvn package -DskipTests'
			}
		}

		stage('Archive Artifacts') {
			steps {
				archiveArtifacts artifacts: 'target/*.jar,target/*.war',
					fingerprint: true,
					followSymlinks: false,
					onlyIfSuccessful: true
			}
		}
	}

	post {
		always {
			cleanWs()
		}
		success {
			echo 'Build completed successfully.'
		}
		failure {
			echo 'Build failed, plase check the logs...'
		}
	}
}