// Hashicorp Vault
// env.VAULT_ENGINE_VERSION = env.VAULT_ENGINE_VERSION ?: "1"

// // Prepare to get service principal credential from Vault
// def componentVaultSecretPath = "kv-${APPLICATION}/buildpack/${COMPONENT}/deploy/${WORKFLOW_ENVIRONMENT}"
// def applicationVaultSecretPath = "kv-${APPLICATION}/buildpack/deploy/${WORKFLOW_ENVIRONMENT}"

// def componentVaultSecret = getSecret(componentVaultSecretPath)
// def componentVaultSecretExists = secretExists(componentVaultSecret)
// def vaultSecret

// if(componentVaultSecretExists){
//     println "Using component Vault secret for ${componentVaultSecretPath}."
//     vaultSecret = componentVaultSecret
// } else{
//     println "Using default Vault secret for ${applicationVaultSecretPath}."
//     vaultSecret = getSecret(applicationVaultSecretPath)
// }

// Parse properties
def filename = "./pipeline/properties.yaml"
if(!fileExists(filename)) {
    error("Property file is ${filename} and does not exists")
} else {
    println "Loading property file: ${filename}..."
}

def properties = readYaml file: filename

env.AZURE_RESOURCE_GROUP = properties?.azure?.RESOURCE_GROUP ?: ""
if(!env.AZURE_RESOURCE_GROUP) {
    error("Azure Resource Group is missing in properties.yaml")
}

env.AZURE_WEBAPP_NAME = properties?.azure?.WEBAPP_NAME ?: ""
if(!env.AZURE_WEBAPP_NAME) {
    error("Azure Web App name is missing in properties.yaml")
}

env.AZURE_DEPLOYMENT_SLOT = properties?.azure?.DEPLOYMENT_SLOTS?."${WORKFLOW_ENVIRONMENT}" ?: ""
if(!env.AZURE_DEPLOYMENT_SLOT) {
    println "Deployment Slot not set for '${WORKFLOW_ENVIRONMENT}' environment in properties.yaml. Using default 'PRODUCTION' Deployment Slot."
}

env.AZURE_SUBSCRIPTION_ID = properties?.azure?.SUBSCRIPTION_ID ?: ""
if(!env.AZURE_SUBSCRIPTION_ID) {
    println "Azure Subscription is missing in properties.yaml"
}

env.AZURE_TENANT_ID = properties?.azure?.TENANT_ID ?: ""
if(!env.AZURE_TENANT_ID) {
    println "Azure Tenant ID is missing in properties.yaml"
}

// Calls additional settings for specific project types. Sets default project type to "container"
if (env.PROJECT_TYPE == "container" || env.PROJECT_TYPE.isEmpty()) {
    println "Project type detected, calling azure-appservice-container/prep.groovy"
    runFile steps: this, file: "azure-appservice-container/prep.groovy"
}

withCredentials([usernamePassword(credentialsId: 'MySP', passwordVariable: 'AZURE_CLIENT_SECRET', usernameVariable: 'AZURE_CLIENT_ID')]) {
    // Secrets validations
    if(!env.AZURE_SUBSCRIPTION_ID) {
        error("Azure Subscription ID not found for environment ${WORKFLOW_ENVIRONMENT}!")
    }

    if(!env.AZURE_TENANT_ID) {
        error("Azure Tenant ID not found for environment ${WORKFLOW_ENVIRONMENT}!")
    }

    if(!env.AZURE_CLIENT_ID) {
        error("Azure Client ID not found for environment ${WORKFLOW_ENVIRONMENT}!")
    }

    if(!env.AZURE_CLIENT_SECRET) {
        error("Azure Client Secret not found for environment ${WORKFLOW_ENVIRONMENT}!")
    }

    try {
        // Azure login via AZURE-CLI
        azureLogin steps: steps,
            subscriptionId: env.AZURE_SUBSCRIPTION_ID,
            tenantId:       env.AZURE_TENANT_ID,
            clientId:       env.AZURE_CLIENT_ID,
            clientSecret:   env.AZURE_CLIENT_SECRET
        
    } catch(Exception error){
        error("Error while trying to login to Azure environment: $error")
    }
}

// // Checks if a Vault secret exists
// def secretExists(vaultSecret){
//     try {
//         withVault([vaultSecrets: vaultSecret]) {
//             println "Vault secret found in ${vaultSecret[0].path}..."
//         }

//         return true;
//     } catch (Exception e){
//         println "Unable to find secret in ${vaultSecret[0].path}: ${e}"
//     }

//     return false;
// }

// // Builds Vault secret object
// def getSecret(vaultSecretPath) {
//     vaultSecretPath = vaultSecretPath.toLowerCase().trim()

//     def vaultSecret = [
//         [   path: "${vaultSecretPath}", 
//             engineVersion: "${env.VAULT_ENGINE_VERSION}", 
//             secretValues: [
//                 [ envVar: 'AZURE_CLIENT_ID', vaultKey: 'clientId'],
//                 [ envVar: 'AZURE_CLIENT_SECRET', vaultKey: 'clientSecret'],
//                 [ envVar: 'AZURE_SUBSCRIPTION_ID', vaultKey: 'subscriptionId'],
//                 [ envVar: 'AZURE_TENANT_ID', vaultKey: 'tenantId']
//             ]
//         ]
//     ]

//     return vaultSecret
// }