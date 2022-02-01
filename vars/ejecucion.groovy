/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
  pipeline {
    agent any
    environment {
        NEXUS_USER         = credentials('nexus-user')
        NEXUS_PASSWORD     = credentials('nexus-password')
    }
    parameters {
        choice(
            name:'compileTool',
            choices: ['Maven', 'Gradle'],
            description: 'Seleccione herramienta de compilacion'
        )
    }
    stages {
      stage("Pipeline"){
        steps {
          script{
            switch(params.compileTool){
                case 'Maven':
                  def ejecucion = load 'maven.groovy'
                  ejecucion.call()
                break;
                case 'Gradle':
                  def ejecucion = load 'gradle.groovy'
                  ejecucion.call()
                break;
            }
          }
        }
        post{
          success{
              slackSend color: 'good', message: "[Nestor Fuenzalida] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slack-token'
          }
          failure{
              slackSend color: 'danger', message: "[Nestor Fuenzalida] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'slack-token'
          }
        }
      }
    }
  }

}

return this;