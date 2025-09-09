import io.github.wsyong11.gameforge.project.artifactId
import io.github.wsyong11.gameforge.project.getOutputFile

artifactId = "Assets"


val generateAssetListTask = tasks.register("generateAssetList") {
	val outputFile = getOutputFile("resources/resource_list.txt")
	val inputDir = file("src/main/resources/")

	inputs.files(inputDir)
	outputs.file(outputFile)

	doLast {
		val resources = inputDir
			.walkTopDown()
			.filterNot { it.isDirectory }
			.map { file ->
				val filePath = file
					.relativeTo(inputDir)
					.toString()
					.replace('\\', '/')
				val fileSize = file.length()
				filePath + "\t" + fileSize
			}
			.joinToString("\n")

		outputFile.asFile.parentFile.mkdirs()
		outputFile.asFile.writeText(resources)
	}
}

tasks.processResources {
	dependsOn(generateAssetListTask)
    from(generateAssetListTask.map { it.outputs.files })
}
