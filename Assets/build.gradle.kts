import io.github.wsyong11.gameforge.project.artifactId
import io.github.wsyong11.gameforge.project.getOutputFile
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

artifactId = "Assets"


val generateAssetListTask = tasks.register("generateAssetList") {
	val outputFile = getOutputFile("resources/resource_list")
	val inputDir = file("src/main/resources/")

	inputs.files(inputDir)
	outputs.file(outputFile)

	doLast {
		val resources = inputDir
			.walkTopDown()
			.filterNot { it.isDirectory }
			.toList()

		val stream = ByteArrayOutputStream()
		val dataStream = DataOutputStream(stream)

		dataStream.writeInt(resources.size)

		for (resource in resources) {
			val path = resource.relativeTo(inputDir).toString().replace('\\', '/')
			val fileSize = resource.length()

			dataStream.writeUTF(path)
			dataStream.writeLong(fileSize)
		}

		outputFile.asFile.parentFile.mkdirs()
		outputFile.asFile.writeBytes(stream.toByteArray())
	}
}

tasks.processResources {
	dependsOn(generateAssetListTask)
	from(generateAssetListTask.map { it.outputs.files })
}
