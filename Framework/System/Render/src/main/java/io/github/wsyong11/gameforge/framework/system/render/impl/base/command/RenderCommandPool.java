package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

public class RenderCommandPool {
	private volatile RenderCommand[] heads;

	public RenderCommandPool() {
	}

	public RenderCommand obtain(int typeId) {
		RenderCommand head = heads[typeId];
		if (head != null) {
			heads[typeId] = head.next;
			return head;
		}
		return create(typeId);
	}

	public void free(RenderCommand cmd) {
		int id = cmd.typeId();
		cmd.reset();
		cmd.next = heads[id];
		heads[id] = cmd;
	}
}
