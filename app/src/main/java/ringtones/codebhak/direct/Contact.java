package ringtones.codebhak.direct;

public class Contact {

	private int id;
	private String name;

	public Contact() {
		this.setId(0);
		this.setName("");
	}

	public Contact(int id, String name) {
		this.setId(id);
		this.setName(name);
	}

	public Contact(Contact contact) {
		this.setId(contact.getId());
		this.setName(contact.getName());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
