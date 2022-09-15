package ku.cs.services;

import ku.cs.models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountListDataSource implements DataSource<AccountList>{
    private String directory;
    private String fileName;
    public AccountListDataSource(String directory,String fileName) { // อธิบายก่อน
        this.directory = directory;
        this.fileName = fileName;
        checkFileIsExisted();
    }
    public AccountListDataSource() {
        this.directory = "data";
        this.fileName = "account.csv";
    }
    @Override
    public AccountList readData() {
        AccountList accountList = new AccountList();
        File file = new File(directory + File.separator + fileName);
        FileReader reader = null; //open for read
        BufferedReader buffer = null;
        String line;
        try {
            reader = new FileReader(file,UTF_8);
            buffer = new BufferedReader(reader);
            while ((line = buffer.readLine()) != null) {
                String[] data = line.split(",");
                String userName = data[1].trim(); //username
                String accountName = data[2].trim(); // accountName
                String password = data[3].trim(); // pass
                String time = data[4].trim(); // time
                String banned = data[5].trim(); // ban
                String profilePicPath = data[6].trim(); // pic
                String team = data[7].trim(); // team
                if (data[0].trim().equals("Student")) {
                    Student student = new Student(accountName, userName, password, profilePicPath);
                    student.setLastLoginTime();
                    accountList.addAccount(student);
                } else if (data[0].trim().equals("Staff")) {
                    Staff staff = new Staff(accountName, userName, password,profilePicPath,team);
                    accountList.addAccount(staff);
                } else if (data[0].trim().equals("Admin")) {
                    Admin admin = new Admin(accountName, userName, password);
                    accountList.addAccount(admin);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ไม่สามารถหาไฟล์" + fileName + "พบ");
        } catch (IOException e) {
            System.out.println("อ่านไฟล์" + fileName + "ไม่ได้");
        }
        finally {
            try {
                buffer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return accountList;
    }
    @Override
    public void writeData(AccountList accountList) { //เขียนทับไปเลย
        File file = new File(directory + File.separator + fileName);
        FileWriter writer = null;
        BufferedWriter buffer = null;
        try {
            writer = new FileWriter(file, UTF_8);
            buffer = new BufferedWriter(writer);
            for (Account account : accountList.getAllAccount()){ //
                if (account.getAccessRights().equals("Student")) {
                    Student student = (Student) account; //cast จาก account ไป student
                    String line = student.toString();
                    buffer.append(line); //ต่อ
                    buffer.newLine();
                }
                else if (account.getAccessRights().equals("Staff")) {
                    Staff staff = (Staff) account;
                    String line = staff.toString();
                    buffer.append(line);
                    buffer.newLine();
                }
                else if ( account.getAccessRights().equals("Admin")){
                    Admin admin = (Admin) account;
                    String line = admin.toString();
                    buffer.append(line);
                    buffer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("เขียนไฟล์" + fileName + "ไม่ได้");
        }
        finally {
            try {
                buffer.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void checkFileIsExisted(){
        File file = new File(directory);
        if (! file.exists()){
            file.mkdirs();
        }
        String filePath = directory + File.separator + fileName; // ย้อนแซค
        file = new File(filePath);
        if (! file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace(); // tell fail
            }
        }

    }
}
