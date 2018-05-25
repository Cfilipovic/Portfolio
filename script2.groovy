@Library(['adtran-pipeline-library@v1.7.0', 'mosaic-os-pipeline-library@v4.2.0']) _

import groovy.transform.Field
import groovy.json.JsonOutput

@Field def buildParameters
@Field def resultStash = [:]

pipeline {
    agent none

    stages {
        stage('Tinderbox') {
            agent none
            steps {
                script {
                  def testSuites = [:]
                  withAutoCleanNode('lightweight') {
                        dir(UUID.randomUUID().toString()) {
                          sh 'curl http://172.22.117.62/tar/test1.tar.gz | tar -xz'
                          sh 'ls'
                          sh "make docker-clean"
                          def testParams = readJSON file: 'parameters.json'
                          stash name: testParams['TEST_SUITE'], allowEmpty: false
                          testSuites.put(testParams['TEST_SUITE'], testParams['HYDRA_RESOURCE_TYPE'])
                        }
                    }
                    // testSuites.each { testSuite, resourceType ->
                    //   withHydraResource(resourceType) { topologyUrl ->
                    //       withAutoCleanNode('lightweight') {
                    //           println 'light test'
                    //           unstash testSuite
                    //           //downloadUrl(topologyUrl)
                    //           //writeJSON file: 'build_parameters.json', json: buildParameters
                    //           try {
                    //               sh "make pipeline"
                    //           } finally {
                    //               // dir('test_results') {
                    //               //     stash name: testSuite, allowEmpty: false
                    //               // }
                    //               //
                    //               // dir(UUID.randomUUID().toString()) {
                    //               //     unstash resultStash['Tinderbox']
                    //               //     dir(testSuite) {
                    //               //         unstash testSuite
                    //               //     }
                    //               //     stash name: resultStash['Tinderbox'], allowEmpty: false
                    //               // }
                    //               //
                    //               // if (fileExists('debug_artifacts')) {
                    //               //     sh "tar -czvf ${testSuite}_debug_artifacts.tar.gz debug_artifacts"
                    //               //     archive "${testSuite}_debug_artifacts.tar.gz"
                    //               // }
                    //               sh "make docker-clean"
                    //           }
                    //       }
                    //   }
                    //}
                }
            }
            post {
                always {
                    script {
                       println 'Always post'
                    }
                }
            }
        }
    }
}
