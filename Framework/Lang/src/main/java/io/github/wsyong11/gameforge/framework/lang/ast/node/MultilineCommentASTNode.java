package io.github.wsyong11.gameforge.framework.lang.ast.node;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MultilineCommentASTNode extends AbstractASTNode<MultilineCommentASTNode> {
	private final String comment;

	public MultilineCommentASTNode(@NotNull String comment) {
		Objects.requireNonNull(comment, "comment is null");
		this.comment = comment;
	}

	@NotNull
	public String getComment() {
		return this.comment;
	}

	@Override
	protected boolean hasChildren() {
		return false;
	}

	@NotNull
	@Override
	protected MultilineCommentASTNode copySelf(@NotNull List<ASTNode> clonedChildren) {
		return new MultilineCommentASTNode(this.comment);
	}

	@Override
	public String toString() {
		return "MultilineComment(\"" + StringEscapeUtils.escapeJava(this.comment) + "\")";
	}
}
