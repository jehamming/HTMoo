package htmoo;

public class MooUser {

	private String id;
	private String name;
	private boolean idle;
	private boolean away;
	private boolean invisible;
	private String icon;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}

	public boolean isAway() {
		return away;
	}

	public void setAway(boolean away) {
		this.away = away;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public static MooUser fromString(String s) {
		MooUser user = new MooUser();

		String[] raw = s.trim().split(",");
		if (raw.length >= 3) {
			// 0 = id
			String id = raw[0].trim();
			String name = raw[1].trim().replaceAll("\"", "");
			String icon = raw[2].trim();
			user.setId( id );
			user.setName(name);
			user.setIcon(icon);
		}

		return user;
	}
	
	public void copyData( MooUser u ) {
		this.setId( u.id );
		this.setName(u.getName());
		this.setAway( u.isAway());
		this.setIcon(u.getIcon());
		this.setIdle(u.isIdle());
		this.setInvisible(u.isInvisible());
	}

	@Override
	public String toString() {
		return getName();
	}

}
