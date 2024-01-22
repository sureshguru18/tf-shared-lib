#! /usr/bin/env groovy

def call(body) {
  def pipelineParams= [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = pipelineParams
  body()

  pipeline {
    agent none
    options {
      ansiColor('xterm')
      disableConcurrentBuilds()
    }
    stages {
        stage('Example') {
            steps {
              script {
                catchError(buildResult: 'SUCCESS', stageResult: 'ABORTED', message: 'There are no changes. Aborting...') {
                  env.DIR_CHANGED = sh(returnStdout: true, script:'''
                    git diff origin/master... --name-only \
                    | grep -Ev ".gitignore|Jenkinsfile|.tfsec" \
                    | awk 'BEGIN{FS=OFS=\"/\"}{NF--; print|"sort -u"}' \
                    | grep -Ev "prod|staging"
                  ''').trim()
                  echo $DIR_CHANGED
                }
              }
            }
        }
    }
}
}  
