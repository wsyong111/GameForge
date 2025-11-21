#version 330 core

in vec4 v_Color;    // 顶点插值过来的颜色
out vec4 fragColor; // 输出到屏幕的颜色

void main()
{
    fragColor = v_Color; // 最朴素的直通
}
