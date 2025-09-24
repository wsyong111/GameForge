package io.github.wsyong11.gameforge.framework.lang.glsl.token;

import io.github.wsyong11.gameforge.framework.lang.token.TokenRules;
import io.github.wsyong11.gameforge.framework.lang.token.Tokenizer;

import java.util.List;
import java.util.Set;

public class GlslTokenizer extends Tokenizer {
	public static final Set<String> KEYWORDS = Set.of(
		// 存储修饰符
		"attribute", "const", "uniform", "varying", "layout",
		"centroid", "flat", "smooth", "noperspective",
		"patch", "sample", "subroutine",

		// 控制流
		"break", "continue", "do", "for", "while",
		"switch", "case", "default",
		"if", "else",

		// 输入输出
		"in", "out", "inout",

		// 基本类型
		"float", "double", "int", "uint", "void", "bool",
		"true", "false",

		// 精度限定
		"lowp", "mediump", "highp", "precision",

		// 修饰符
		"invariant",

		// 特殊指令
		"discard", "return",

		// 矩阵类型
		"mat2", "mat3", "mat4",
		"dmat2", "dmat3", "dmat4",
		"mat2x2", "mat2x3", "mat2x4",
		"dmat2x2", "dmat2x3", "dmat2x4",
		"mat3x2", "mat3x3", "mat3x4",
		"dmat3x2", "dmat3x3", "dmat3x4",
		"mat4x2", "mat4x3", "mat4x4",
		"dmat4x2", "dmat4x3", "dmat4x4",

		// 向量类型
		"vec2", "vec3", "vec4",
		"ivec2", "ivec3", "ivec4",
		"uvec2", "uvec3", "uvec4",
		"bvec2", "bvec3", "bvec4",
		"dvec2", "dvec3", "dvec4",

		// 采样器类型
		"sampler1D", "sampler2D", "sampler3D", "samplerCube",
		"sampler1DShadow", "sampler2DShadow", "samplerCubeShadow",
		"sampler1DArray", "sampler2DArray",
		"sampler1DArrayShadow", "sampler2DArrayShadow",
		"isampler1D", "isampler2D", "isampler3D", "isamplerCube",
		"isampler1DArray", "isampler2DArray",
		"usampler1D", "usampler2D", "usampler3D", "usamplerCube",
		"usampler1DArray", "usampler2DArray",
		"sampler2DRect", "sampler2DRectShadow",
		"isampler2DRect", "usampler2DRect",
		"samplerBuffer", "isamplerBuffer", "usamplerBuffer",
		"sampler2DMS", "isampler2DMS", "usampler2DMS",
		"sampler2DMSArray", "isampler2DMSArray", "usampler2DMSArray",
		"samplerCubeArray", "samplerCubeArrayShadow",
		"isamplerCubeArray", "usamplerCubeArray",

		// 结构体
		"struct"
	);

	public static final Set<String> OPERATORS = Set.of(
		// 括号与分隔符
		"{", "}", "(", ")", "[", "]",
		",", ".", ";", "?", ":", "::",

		// 预处理符号
		"#", "##",

		// 赋值与算术
		"=", "+", "++", "+=", "-", "--", "-=",
		"*", "*=", "/", "/=", "%", "%=",

		// 比较
		">", ">=", "==", "<=", "<", "!=",

		// 逻辑
		"!", "&&", "||",

		// 位运算
		"&", "&=", "|", "|=", "^", "^=",
		">>", ">>=", "<<", "<<=", "~"
	);

	public GlslTokenizer() {
		super(List.of(
			TokenRules.ofComment("//"),
			TokenRules.ofMultiLineComment("/*", "*/"),
			TokenRules.ofOperator(OPERATORS),
			TokenRules.ofNumber(),
			TokenRules.ofKeywords(KEYWORDS)
		));
	}
}
