// Parse properties used by "azure-appservice-container" project type
def filename = "${GIT_PROJECT_DIR}/pipeline/properties.yaml"
if(!fileExists(filename)) {
    error("Property file is ${filename} and does not exists")
} else {
    println "Loading property file: ${filename}..."
}

def properties = readYaml file: filename

env.IMAGE_REGISTRY = properties?.docker?.IMAGE_REGISTRY ?: ""
if(!env.IMAGE_REGISTRY) {
    error("Container Image Registry URL is missing in properties.yaml")
}

env.IMAGE_NAME = properties?.docker?.IMAGE_NAME ?: ""
if(!env.IMAGE_NAME) {
    error("Container Image Name is missing in properties.yaml")
}