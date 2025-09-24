package io.github.wsyong11.gameforge.framework.lang.ast.node;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.List;

public interface ASTNode extends Iterable<ASTNode> {
	/**
	 * 获取父节点
	 *
	 * @return 父节点，如果未添加到树或为根节点则返回 null
	 */
	@Nullable
	ASTNode getParent();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取子节点列表
	 *
	 * @return 子节点列表的只读视图
	 */
	@NotNull
	@UnmodifiableView
	List<ASTNode> getChildren();

	/**
	 * 添加子节点
	 *
	 * @param node 子节点对象，只允许添加可变节点
	 * @throws IllegalArgumentException 尝试添加只读节点或节点已被添加到其他树时抛出
	 * @throws IllegalStateException    此节点已被冻结时抛出
	 */
	void addNode(@NotNull ASTNode node);

	/**
	 * 设定子节点
	 *
	 * @param index 索引
	 * @param node  子节点对象，只允许添加可变节点
	 * @return 旧的子节点对象
	 * @throws IllegalArgumentException  尝试添加只读节点或节点已被添加到其他树时抛出
	 * @throws IllegalStateException     此节点已被冻结时抛出
	 * @throws IndexOutOfBoundsException 索引超出范围
	 */
	@NotNull
	ASTNode setNode(int index, @NotNull ASTNode node);

	/**
	 * 移除子节点
	 *
	 * @param node 节点对象，如果传递 null 则忽略
	 * @throws IllegalStateException 此节点已被冻结时抛出
	 */
	void removeNode(@Nullable ASTNode node);

	/**
	 * 获取子节点
	 *
	 * @param index 索引
	 * @return 子节点对象
	 * @throws IndexOutOfBoundsException 索引超出范围
	 */
	@NotNull
	default ASTNode getNode(int index){
		return this.getChildren().get(index);
	}

	/**
	 * 获取子节点数量
	 *
	 * @return 子节点数量
	 */
	default int childrenCount() {
		return this.getChildren().size();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取源代码信息
	 *
	 * @return 源代码信息，在动态生成的节点上可能为 null
	 */
	@Nullable
	SourceInfo getSource();

	/**
	 * 将节点和所有子节点全部转换为只读节点，多次调用将被忽略
	 */
	void freeze();

	/**
	 * 检查此节点是否为只读节点
	 *
	 * @return 当前节点是否为只读节点
	 */
	boolean isFrozen();

	/**
	 * 从当前节点上深度拷贝为可变节点
	 *
	 * @return 可变节点对象
	 */
	@NotNull
	ASTNode deepCopy();

	/**
	 * 获取遍历当前子节点的迭代器
	 *
	 * @return 子节点列表迭代器
	 */
	@NotNull
	@Override
	Iterator<ASTNode> iterator();
}
