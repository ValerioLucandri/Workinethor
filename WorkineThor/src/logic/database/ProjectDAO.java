package logic.database;

import java.sql.SQLException;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.logging.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import logic.bean.ProjectBean;
import logic.exceptions.ProjectAlreadyExistsException;
import logic.model.Project;
import logic.model.Session;
import logic.model.User;

public class ProjectDAO {

	private DBhandle handle = DBhandle.getDBhandleInstance();
	private Connection dbConnection;
	private PreparedStatement statement;

	/**
	 * method that adds the created project by the active user(found in Session)
	 * 
	 * @param project
	 * @param session
	 * @throws ProjectAlreadyExistsException 
	 */
	public boolean addProjectToDB(Project project, Session session) throws ProjectAlreadyExistsException {
		Logger logger = Logger.getLogger(ProjectDAO.class.getName());
		String sqlString = "INSERT INTO projects(project_name, drive_name, user)" + "VALUES (?,?,?)";
		boolean result = false;
		
		ProjectBean bean = new ProjectBean();
		bean.setProjectName(project.getProjectName());
		result = checkIfProjectExist(bean);
		
		if(result) {
			try {
				statement = dbConnection.prepareStatement(sqlString);
				statement.setString(1, project.getProjectName());
				statement.setString(2, project.getDriveName());
				statement.setString(3, session.getLoggedUser().getUsername());
				statement.executeUpdate();
				
				addMemberToProject(bean, session.getLoggedUser().getUsername());
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "SQL query failed!!");
				return false;
			}

			logger.log(Level.INFO, "project:" + project.getProjectName() + " added");
		}
		
		return true;
	}
	
	public boolean checkIfProjectExist(ProjectBean bean) throws ProjectAlreadyExistsException {
		Logger logger = Logger.getLogger(ProjectDAO.class.getName());
		String sqlFindString = "SELECT project_name FROM projects WHERE project_name = ?";
		boolean result = false;
		
		dbConnection = handle.getConnection();

		try {
			statement = dbConnection.prepareStatement(sqlFindString);
			statement.setString(1, bean.getProjectName());
			ResultSet rs = statement.executeQuery();
			
			if(!rs.first()) {
				result = true;
				logger.log(Level.INFO, "project with name:"+bean.getProjectName()+" does not exist");
			}
			else {
				result = false;
				throw new ProjectAlreadyExistsException("project:"+bean.getProjectName()+" already exists");
			}
		} catch (SQLException e1) {}
		
		return result;
	}
	
	public void addMemberToProject(ProjectBean projectBean, String member) {
		String sqlInsertMembers = "INSERT INTO members(project, member) VALUES (?,?)";
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(sqlInsertMembers);
			statement.setString(1, projectBean.getProjectName());
			statement.setString(2, member);
			
			statement.executeUpdate();
		} catch (SQLException e) {}
	}
	
	public boolean addUserInProjectToDB(ProjectBean bean, String user) {
		Logger logger = Logger.getLogger(ProjectDAO.class.getName());
		String sqlString = "INSERT INTO projects(project_name, drive_name, user)" + "VALUES (?,?,?)";

		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, bean.getProjectName());
			statement.setString(2, bean.getDriveName());
			statement.setString(3, user);
			statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "SQL query failed!!");
			return false;
		}

		logger.log(Level.INFO, "user:" + user + " added");
		return true;
	}
	

	public boolean removeProjectFromDB(Project project) {
		Logger logger = Logger.getLogger(ProjectDAO.class.getName());
		String sqlString = "DELETE FROM projects WHERE " + "project_name = ?";

		dbConnection = handle.getConnection();

		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, project.getProjectName());
			statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "SQL query failed!!");
			return false;
		}

		logger.log(Level.INFO, "project:" + project.getProjectName() + " removed");
		return true;
	}

	public ArrayList<Project> getActiveUserProjectsFromDB() {
		ArrayList<Project> tempProjects = new ArrayList<>();
		String sqlString = "SELECT * FROM projects WHERE " + "user = ?";
		ResultSet resultSet;

		dbConnection = handle.getConnection();

		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, Session.getSession().getLoggedUser().getUsername());

			resultSet = statement.executeQuery();
			
			if(!resultSet.first()) {}
			else {
				Project temp;
				do {
					temp = new Project();
					temp.setProjectName(resultSet.getString("project_name"));
					temp.setDriveName(resultSet.getString("drive_name"));
					if(resultSet.getString("user") != null) {
						User user = new User();
						user.setUsername(resultSet.getString("user"));
						temp.addMember(user);
					}
					tempProjects.add(temp);
				}while(resultSet.next());
			}
		} catch (SQLException e) {
		}
		return tempProjects;
	}

	public ObservableList<String> getAllUserProjects(User user) {
		ObservableList<String> projectStrings = FXCollections.observableArrayList();
		String sqlString = "SELECT * FROM projects WHERE " + "user = ?";

		dbConnection = handle.getConnection();

		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, user.getUsername());

			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.first()) {

			} else {
				projectStrings.addAll(resultSet.getString("project_name"));
				while (resultSet.next()) {
					projectStrings.addAll(resultSet.getString("project_name"));
				}
			}
		} catch (SQLException e) {
		}
		return projectStrings;
	}

	public ObservableList<String> getAllProjects() {
		String getAllProjects = "SELECT DISTINCT project_name FROM projects";
		ObservableList<String> projects = FXCollections.observableArrayList();

		dbConnection = handle.getConnection();

		try {
			statement = dbConnection.prepareStatement(getAllProjects);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (!rs.first()) {

			} else {
				projects.addAll(rs.getString("project_name"));
				while (rs.next())
					projects.addAll(rs.getString("project_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return projects;
	}
	
	public String getProjectAdmin(ProjectBean bean) {
		String admin = "";
		String sqlString = "SELECT user FROM projects WHERE project_name = ?";
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, bean.getProjectName());
			
			ResultSet rs = statement.executeQuery();
			
			if(!rs.first()) {}
			else {
				admin = rs.getString("user");
			}
		} catch (SQLException e) {}
		
		return admin;
	}
	
	public ObservableList<String> getAllProjectUsers(ProjectBean bean){
		String getAllUsers = "SELECT member FROM members WHERE project = ? ";
		ObservableList<String> users = FXCollections.observableArrayList();
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(getAllUsers);
			statement.setString(1, bean.getProjectName());
			
			ResultSet rs = statement.executeQuery();
			if(!rs.first()) {}
			else {
				users.addAll(rs.getString("member"));
				while(rs.next())
					users.addAll(rs.getString("member"));
			}
		} catch (SQLException e) {}
		
		return users;
	}

	public ObservableList<String> getAllUsersNotInProject(ProjectBean bean){
		String getAllUsers = "SELECT DISTINCT user FROM projects WHERE project_name != ?";
		ObservableList<String> users = FXCollections.observableArrayList();
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(getAllUsers);
			statement.setString(1, bean.getProjectName());
			
			ResultSet rs = statement.executeQuery();
			if(!rs.first()) {
			}else{
				users.addAll(rs.getString("user"));
				while(rs.next())
					users.addAll(rs.getString("user"));
				}
			
			} catch (SQLException e) {			
		}
		return users;
	}
	
	public Project getProjectFromDB(ProjectBean bean) {
		String sqlString = "SELECT DISTINCT project_name, drive_name, user FROM projects WHERE project_name = ?";
		Project temp = null;
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, bean.getProjectName());
			
			ResultSet rs = statement.executeQuery();
			
			if(rs.first()) {
				temp = new Project();
				temp.setProjectName(rs.getString("project_name"));
				temp.setDriveName(rs.getString("drive_name"));
				if(temp.getDriveName() != null)
					temp.setDriveActive(true);
				else 
					temp.setDriveActive(false);
			}
		} catch (SQLException e) {}
		return temp;
	}
	
	public ObservableList<String> getMembersToAdd(ProjectBean bean){
		String sqlString = "SELECT username FROM users EXCEPT SELECT member FROM members WHERE project = ?";
		ObservableList<String> retList = FXCollections.observableArrayList();
		
		dbConnection = handle.getConnection();
		
		try {
			statement = dbConnection.prepareStatement(sqlString);
			statement.setString(1, bean.getProjectName());
			
			ResultSet rs = statement.executeQuery();
			
			if(!rs.first()) {}
			else {
				do {
					retList.addAll(rs.getString("username"));
				}while(rs.next());
			}
		} catch (SQLException e) {}
		return retList;
	}
}
