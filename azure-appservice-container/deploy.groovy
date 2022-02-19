try {
    // Updates webapp with new Docker image via AZURE-CLI
    azureSetWebAppContainer steps: steps,
        resourceGroup: env.AZURE_RESOURCE_GROUP,
        webappName:    env.AZURE_WEBAPP_NAME,
        imageWithTag: env.IMAGE_NAME,
        containerRegistryURL: env.IMAGE_REGISTRY,
        containerRegistryUser: env.AZURE_CLIENT_ID, //DEPS_CREDENTIAL_USR,
        containerRegistryPwd: env.AZURE_CLIENT_SECRET //DEPS_CREDENTIAL_PSW,
        deploymentSlot: env.AZURE_DEPLOYMENT_SLOT ?: ""
    
} catch(Exception error){
    error("Error while updating WebApp container on Azure environment: $error")
}