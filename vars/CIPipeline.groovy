pipeline {
    agent any
    options {
        // Timeout counter starts AFTER agent is allocated
        timeout(time: 1, unit: 'SECONDS')
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
