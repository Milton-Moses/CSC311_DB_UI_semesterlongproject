package model;

/**
 * Represents a person with personal and academic information.
 * This class models the data structure for individuals in the system.
 */
public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private String department;
    private String major;
    private String email;
    private String imageURL;

    /**
     * Default constructor for Person.
     */
    public Person() {
    }

    /**
     * Constructs a Person without ID (used when creating new records).
     * @param firstName the first name
     * @param lastName the last name
     * @param department the department
     * @param major the major
     * @param email the email address
     * @param imageURL URL of the person's image
     */
    public Person(String firstName, String lastName, String department, String major, String email, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.email = email;
        this.imageURL = imageURL;
    }

    /**
     * Constructs a Person with all fields including ID.
     * @param id the unique identifier
     * @param firstName the first name
     * @param lastName the last name
     * @param department the department
     * @param major the major
     * @param email the email address
     * @param imageURL URL of the person's image
     */
    public Person(Integer id, String firstName, String lastName, String department, String major, String email, String imageURL) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.major = major;
        this.email = email;
        this.imageURL = imageURL;
    }

    // Getters and setters with Javadoc comments
    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the ID to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the major
     */
    public String getMajor() {
        return major;
    }

    /**
     * @param major the major to set
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return the image URL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL the image URL to set
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return string representation of the Person
     */
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}