#version 330 core

// 来自 CPU 的矩阵（由你的 engine 设置）
uniform mat4 u_Model;
uniform mat4 u_View;
uniform mat4 u_Projection;

// 来自 Mesh 的顶点属性
layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec4 a_Color;

// 传给片段着色器
out vec4 v_Color;

void main()
{
    v_Color = a_Color;
    gl_Position = u_Projection * u_View * u_Model * vec4(a_Position, 1.0);
}
