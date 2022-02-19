@Library("global") _

pipeline {
	agent any

	environment {
		BUILDPACK = "azure-appservice"
		PROJECT_TYPE = "azure-appservice-container"

		DEBUG = false
		START_BUILD = new Date().format("EEE, MMM dd yyyy - HH:mm:ss")
		
		// CREDENCIAIS BASE DO PIPELINE COMMIT
		// GIT_CREDENTIAL_ID	= "gitlab"
		// DEPS_CREDENTIAL_ID	= "nexus_devops"
        // PUB_CREDENTIAL_ID	= "nexus_devops"
		// GIT_CREDENTIAL 		= credentials("${GIT_CREDENTIAL_ID}")
		// DEPS_CREDENTIAL		= credentials("${DEPS_CREDENTIAL_ID}")
		// PUB_CREDENTIAL 		= credentials("${PUB_CREDENTIAL_ID}")
		// VAULT_URL			= "http://vault.devops.redecorp.br"

		// WORKFLOW DE RASTREABILIDADE E QUALITY GATE
		WORKFLOW_PIPELINE 	= "Deploy"

  		// TODO: revisar parâmetros
      	GIT_PROJECT_DIR 			= "./src"
      	GIT_REPOSITORY_BRANCH 		= "main"

		// Pipeline configuration
		SCRIPT_LOCATION 			= "."
		// DEVOPS_REPOSITORY 			= "devops-release"
		// JDK_VERSION 				= "8u192"
		// MAVEN_CONTAINER 			= "jnlp"
		// MAVEN_STRATEGY 				= "plugin"
		// MAVEN_VERSION 				= "3.6.2"
		// MAVEN_SETTINGS 				= "settings.xml"
		// MAVEN_SETTINGS_REMOTE 		= "settings/devops-release.xml"

		// COLORS
	    BOLD    = "\033[1;30m"
	    OFF     = "\033[0m"
	    INFO    = "\033[1;94mINFO\033[0m"
	    WAITING = "\033[1;94mWAITING\033[0m"
	    ERROR   = "\033[1;91mERROR\033[0m"
	    FAILED  = "\033[1;91mFAILED\033[0m"
	    SUCCESS = "\033[1;92mSUCCESS\033[0m"
	    WARNING = "\033[1;93mWARNING\033[0m"
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
				println this
			}
		}

		stage("Preparação") {
			steps {
				script {
					runFile steps:this,
						file: "${env.BUILDPACK}/prep.groovy"
				}
			}
		}

		stage('Dependências') {
			steps {
				script {
					runFile steps: this,
						file: "${env.BUILDPACK}/deps.groovy"
				}
			}
		}

		stage('Deploy') {
			steps {
				script {
					runFile steps:this,
						file: "${env.BUILDPACK}/deploy.groovy"
				}
			}
		}
	}

}
