
int PORT = env.BUILD_NUMBER.toInteger() + 1000
int TRY  = 10
node {
    stage('Deploy Docker') { 
        sh "docker run -d -p $PORT:80 $IMAGE:$TAG"

        int i = 0;
        while(i < TRY) {
            try {
                sleep(1)
                new URL("http://localhost:$PORT").openConnection().getResponseCode()
                break
            } catch(Exception $ex) {
                if(++i >= TRY) {
                    throw $ex
                }
                print $ex
            }
        }
    }

    stage('Reload Nginx') { 
        print 'reload nginx ...'
       
    }
    
    stage('Destroy Old') {
        def containers = sh(returnStdout: true, script: " docker ps | grep $IMAGE | awk '{print \$1 \" \" \$2}' ")
        print containers
        containers = containers.split('\n')
        print containers
        for(int i = 0; i < containers.length; i++) {
            sh "docker stop ${containers[i].split(' ')[0]} || true"
            sh "docker rm ${containers[i].split(' ')[0]} || true"
        }
    }
}

