package io.github.wsyong11.gameforge.framework.system.render.mesh.loader;

import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import io.github.wsyong11.gameforge.framework.system.render.mesh.ex.MeshLoadException;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjMeshLoader implements MeshLoader {
	@NotNull
	@Override
	public Mesh load(@NotNull InputStream stream) throws IOException {
		Objects.requireNonNull(stream, "stream is null");

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

		try {
			return new Parser(reader).parse();
		} catch (SyntaxException e) {
			throw new MeshLoadException(e);
		}
	}

	private static class Parser {
		private final BufferedReader reader;

		private int lineIndex;
		private String currentLineSource;

		// 3 float Vector3f
		private final FloatArrayList vertexPositions;
		// 2 float Vector2f
		private final FloatArrayList vertexUvs;
		// 3 float
		private final FloatArrayList vertexNormals;

		// 3 float
		private final IntArrayList positionIndices;
		// 3 float
		private final IntArrayList uvIndices;
		// 3 float
		private final IntArrayList normalIndices;

		public Parser(@NotNull BufferedReader reader) {
			Objects.requireNonNull(reader, "reader is null");

			this.reader = reader;

			this.lineIndex = 0;
			this.currentLineSource = "";

			this.vertexPositions = new FloatArrayList();
			this.vertexUvs = new FloatArrayList();
			this.vertexNormals = new FloatArrayList();

			this.positionIndices = new IntArrayList();
			this.uvIndices = new IntArrayList();
			this.normalIndices = new IntArrayList();
		}

		private float parseFloat(@NotNull String text) {
			Objects.requireNonNull(text, "text is null");

			try {
				return Float.parseFloat(text);
			} catch (NumberFormatException e) {
				throw SyntaxException
					.builder("Float syntax error")
					.ofCause(e)
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.build();
			}
		}

		private int parseIndex(@NotNull String text, int size) {
			Objects.requireNonNull(text, "text is null");

			try {
				int i = Integer.parseInt(text);
				return i < 0 ? size + i : i;
			} catch (NumberFormatException e) {
				throw SyntaxException
					.builder("Integer syntax error")
					.ofCause(e)
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.build();
			}
		}

		private void parseVector3(String @NotNull [] parameters, @NotNull FloatList list) {
			Objects.requireNonNull(parameters, "parameters is null");
			Objects.requireNonNull(list, "list is null");

			if (parameters.length < 3)
				throw SyntaxException
					.builder("Missing parameter")
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.build();

			float x = this.parseFloat(parameters[0]);
			float y = this.parseFloat(parameters[1]);
			float z = this.parseFloat(parameters[2]);

			list.add(x);
			list.add(y);
			list.add(z);
		}

		private void processVertex(@NotNull String[] parameters) {
			Objects.requireNonNull(parameters, "parameters is null");
			this.parseVector3(parameters, this.vertexPositions);
		}

		private void processVertexNormal(@NotNull String[] parameters) {
			Objects.requireNonNull(parameters, "parameters is null");
			this.parseVector3(parameters, this.vertexNormals);
		}

		private void parseFaceParameter(int @NotNull [] resultArray, int offset, @NotNull String text) {
			Objects.requireNonNull(resultArray, "resultArray is null");
			Objects.requireNonNull(text, "text is null");

			String[] parameter = text.split("/");
			/*
> * `v` → 顶点索引
> * `v/vt` → 顶点/纹理
> * `v//vn` → 顶点/法线
> * `v/vt/vn` → 顶点/纹理/法线
			 */

			int vertexCount = this.vertexPositions.size() / 3;
			resultArray[offset] = this.parseIndex(parameter[0], vertexCount) - 1;

			if (parameter.length > 1) {
				String uv = parameter[1];
				if (uv.isEmpty()) {
					resultArray[offset + 1] = -1;
				} else {
					int uvCount = this.vertexUvs.size() / 3;
					resultArray[offset + 1] = this.parseIndex(uv, uvCount) - 1;
				}
			} else {
				resultArray[offset + 1] = -1;
			}

			if (parameter.length > 2) {
				String normal = parameter[2];
				if (normal.isEmpty()) {
					resultArray[offset + 2] = -1;
				} else {
					int normalCount = this.vertexNormals.size() / 3;
					resultArray[offset + 2] = this.parseIndex(normal, normalCount) - 1;
				}
			} else {
				resultArray[offset + 2] = -1;
			}
		}

		private void processFace(@NotNull String[] parameters) {
			Objects.requireNonNull(parameters, "parameters is null");

			if (parameters.length < 3)
				throw SyntaxException
					.builder("Missing parameter")
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.build();

			if (parameters.length > 3)
				throw SyntaxException
					.builder("Unsupported parameter count " + parameters.length)
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.build();

			int[] faceParameters = new int[9];
			this.parseFaceParameter(faceParameters, 0, parameters[0]);
			this.parseFaceParameter(faceParameters, 2, parameters[1]);
			this.parseFaceParameter(faceParameters, 5, parameters[2]);

			this.positionIndices.add(faceParameters[0]);
			this.uvIndices.add(faceParameters[1]);
			this.normalIndices.add(faceParameters[2]);

			this.positionIndices.add(faceParameters[3]);
			this.uvIndices.add(faceParameters[4]);
			this.normalIndices.add(faceParameters[5]);

			this.positionIndices.add(faceParameters[6]);
			this.uvIndices.add(faceParameters[7]);
			this.normalIndices.add(faceParameters[8]);
		}

		private void process(@NotNull String command, @NotNull String[] parameters) {
			Objects.requireNonNull(command, "command is null");
			Objects.requireNonNull(parameters, "parameters is null");

			switch (command) {
				case "v" -> this.processVertex(parameters);
				case "vn" -> this.processVertexNormal(parameters);
				case "f" -> this.processFace(parameters);

				case "s" -> { /* skip */ }
				default -> throw SyntaxException
					.builder("Unknown command '" + command + "'")
					.ofLine(this.lineIndex)
					.ofSource(this.currentLineSource)
					.ofColumn(0, command.length())
					.build();
			}
		}

		@NotNull
		public Mesh parse() throws IOException {
			List<String> parameterList = new ArrayList<>();

			String line;
			while ((line = this.reader.readLine()) != null) {
				if (line.isEmpty() || line.isBlank()) {
					this.lineIndex++;
					continue;
				}

				String trimLine = line.trim();

				// Comment
				if (trimLine.startsWith("#")) {
					this.lineIndex++;
					continue;
				}

				parameterList.clear();
				int lineLength = trimLine.length();

				int i = 0;
				int lastIndex = 0;
				while (i < lineLength) {
					int ch = trimLine.codePointAt(i);
					if (Character.isWhitespace(ch)) {
						parameterList.add(trimLine.substring(lastIndex, i));
						lastIndex = i + 1;
					}

					i++;
				}

				if (lastIndex < lineLength)
					parameterList.add(trimLine.substring(lastIndex));

				int parameterCount = parameterList.size();
				if (parameterCount <= 1)
					throw SyntaxException
						.builder("Unknown command")
						.ofLine(this.lineIndex)
						.ofColumn(0, line.length())
						.ofSource(line)
						.build();

				String command = parameterList.get(0);
				String[] parameters = parameterList.subList(1, parameterCount).toArray(String[]::new);

				this.currentLineSource = line;
				this.process(command, parameters);

				this.lineIndex++;
			}

			return null;
		}
	}
}

/*
在 `.obj` 文件里，指令其实就是每行开头的字母，它们告诉解析器这行数据的类型。主要有以下几类：

---

## 1️⃣ 顶点相关

| 指令   | 含义           | 格式示例                   |
| ---- | ------------ | ---------------------- |
| `v`  | 顶点坐标         | `v 0.123 0.234 0.345`  |
| `vt` | 顶点纹理坐标       | `vt 0.500 1.000`       |
| `vn` | 顶点法线         | `vn 0.707 0.000 0.707` |
| `vp` | 参数空间顶点 (较少用) | `vp 0.5 0.5 0.0`       |

---

## 2️⃣ 面相关

| 指令  | 含义          | 示例                    |
| --- | ----------- | --------------------- |
| `f` | 面 (polygon) | `f 1/1/1 2/2/2 3/3/3` |
| `l` | 线段          | `l 1 2 3`             |

> 面的索引格式可以是：
>
> * `v` → 顶点索引
> * `v/vt` → 顶点/纹理
> * `v//vn` → 顶点/法线
> * `v/vt/vn` → 顶点/纹理/法线

---

## 3️⃣ 分组/对象

| 指令  | 含义  | 示例              |
| --- | --- | --------------- |
| `o` | 新对象 | `o Cube`        |
| `g` | 组   | `g FrontFace`   |
| `s` | 平滑组 | `s 1` 或 `s off` |

---

## 4️⃣ 材质相关

| 指令       | 含义      | 示例                   |
| -------- | ------- | -------------------- |
| `mtllib` | 关联材质库文件 | `mtllib cube.mtl`    |
| `usemtl` | 指定材质    | `usemtl Material001` |

---

## 5️⃣ 其他/注释

| 指令       | 含义                  | 示例                     |
| -------- | ------------------- | ---------------------- |
| `#`      | 注释                  | `# 这是注释`               |
| `cstype` | 曲线类型 (Bezier/NURBS) | `cstype bezier`        |
| `deg`    | 曲线/曲面阶数             | `deg 3`                |
| `curv`   | 曲线                  | `curv 1 2 3 4`         |
| `surf`   | 曲面                  | `surf 1 2 3 4 5 6 7 8` |
| `parm`   | 曲线/曲面参数             | `parm u 0.0 0.5 1.0`   |

---

💡 **小提示**

* 常用的主要就是 `v`、`vt`、`vn`、`f`、`o`、`g`、`usemtl`，其他像曲线曲面比较少用。
* 注释行 `#` 可以直接跳过。

---

如果你想，我可以帮你画一个 **OBJ指令总结图**，把顶点、面、材质、曲线全部分类一目了然 📊。

你想要我画吗？

 */