if (env.PROJECT_TYPE == "azure-appservice-container") {
    println "Project type detected, calling azure-appservice-container/deploy.groovy"
    runFile steps: this, file: "azure-appservice-container/deploy.groovy"
    return
}