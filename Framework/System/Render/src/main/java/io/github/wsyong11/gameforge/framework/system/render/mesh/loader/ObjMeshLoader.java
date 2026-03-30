package io.github.wsyong11.gameforge.framework.system.render.mesh.loader;

import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import io.github.wsyong11.gameforge.framework.system.render.mesh.ex.MeshLoadException;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

		private final FloatArrayList vertexPositions;

		public Parser(@NotNull BufferedReader reader) {
			Objects.requireNonNull(reader, "reader is null");

			this.reader = reader;

			this.vertexPositions = new FloatArrayList();
		}

		private boolean processVertex(@NotNull String line) {
			Objects.requireNonNull(line, "line is null");
			assert line.startsWith("v");
			return true;
		}

		@NotNull
		public Mesh parse() throws IOException {
			String line;
			int lineIndex = 0;
			while ((line = this.reader.readLine()) != null) {
				if (line.isEmpty() || line.isBlank())
					continue;

				String trimLine = line.trim();

				// Comment
				if (trimLine.startsWith("#"))
					continue;

				boolean success;
				if (trimLine.startsWith("v")) {
					success = this.processVertex(trimLine);
				} else {
					throw new SyntaxException("Unknown command", lineIndex, line.length(), line);
				}

				lineIndex++;
			}
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