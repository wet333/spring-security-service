pipeline {
	agent any

	tools {
		maven 'Maven'
	}

	triggers {
		pollSCM('H/5 * * * *') // Poll every 5 minutes
	}

	stages {
		stage('Checkout') {
			steps {
				echo 'Checkout project.'
			}
		}

		stage('Build') {
			steps {
				sh 'mvn clean compile'
			}
		}
	}
}