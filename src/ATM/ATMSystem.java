package ATM;

import javax.crypto.spec.PSource;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * 入口类
 */
public class ATMSystem {
    public static void main(String[] args) {
        //1.定义账户类
        //2. 定义集合容器，存储全部账户对象
        ArrayList<Account> accounts = new ArrayList<>();
        //3. 展示系统首页，
        while (true) {
            System.out.println("--------ATM系统---------");
            System.out.println("1, 登录");
            System.out.println("2, 注册");
            Scanner sc = new Scanner(System.in);
            System.out.println("选择操作");
            int command = sc.nextInt();

            switch (command) {
                case 1 ->
                    //登录
                        login(accounts, sc);
                case 2 ->
                    //注册
                        register(accounts, sc);
                default -> System.out.println("没这功能");
            }
        }
    }


    /**
     * 登录功能,
     * @param accounts 全部账户对象集合
     * @param sc 扫描器
     */
    private static void login(ArrayList<Account> accounts, Scanner sc) {
        System.out.println("----------------登录-----------------");
        //1。判断账户集合中是否存在账户，没有的话不能运行
        if(accounts.size() == 0){
            System.out.println("系统中无任何用户，先注册");
            return;//结束方法的执行
        }

        //进入登录操作
        while (true) {
            System.out.println("输入卡号");
            String cardId = sc.next();

            //判断卡号是否存在
            Account acc = getAccountByCardId(cardId, accounts);
            if(acc != null){
                //卡号存在
                //用户输入密码认证
                while (true) {
                    System.out.println("输入密码");
                    String passwd = sc.next();
                    //判断密码是否正确
                    if(acc.getPasswd().equals(passwd)){
                        //登录成功
                        System.out.println(acc.getUsername() + "登录成功，卡号是" + acc.getCardid());
                        //展示登录后的操作页，整个方法
                        showUserCommand(sc, acc, accounts);
                        return;//干掉登录方法

                    }else {
                        System.out.println("输入密码有误");
                    }
                }
            }else {
                System.out.println("系统中不存在该账户卡号");
            }
        }
    }

    /**
     * 展示登录后的操作页面
     */
    private static void showUserCommand(Scanner sc, Account acc, ArrayList<Account> accounts) {
        while (true) {
            System.out.println("--------------用户操作页------------------");
            System.out.println("1, 查询账户");
            System.out.println("2, 存款");
            System.out.println("3, 取款");
            System.out.println("4, 转账");
            System.out.println("5, 修改密码");
            System.out.println("6, 退出");
            System.out.println("7, 销户");
            System.out.println("选择");
            int command = sc.nextInt();
            switch (command) {
                case 1 ->
                    //查询账户，当前登录的账户信息
                        showAccount(acc);
                case 2 ->
                    //存款
                        depositMoney(acc, sc);
                case 3 ->
                    //取款
                        drawMoney(acc, sc);
                case 4 ->
                    //转账
                        transferMoney(sc, acc, accounts);
                case 5 -> {
                    //修改密码
                    updatepasswd(sc, acc);
                    return;//跳出登录
                }
                case 6 -> {
                    //退出
                    System.out.println("退出成功");
                    return;//干掉当前方法的执行
                }
                case 7 -> {
                    //注销
                    if(deleteAccount(acc, sc, accounts)){
                        return;
                    }else {
                        //销户没有成功
                        break;
                    }
                }
                default -> System.out.println("输入的操作命令不正确");
            }
        }
    }

    /**
     * 销户
     * @param acc
     * @param sc
     * @param accounts
     */
    private static boolean deleteAccount(Account acc, Scanner sc, ArrayList<Account> accounts) {
        System.out.println("-----------用户销户-----------");
        System.out.println("真的销户吗 y/n");
        String s = sc.next();
        switch (s){
            case "y":
                //真销户
                if(acc.getMoney() > 0){
                    System.out.println("账户还有钱，不能销户");
                }else {
                    accounts.remove(acc);
                    System.out.println("销户成功");
                    return true;
                }
                break;
            default:
                System.out.println("那就不销户了");
        }
        return false;
    }

    /**
     * 改密码
     * @param sc 扫描器
     * @param acc 当前登录成功的对象
     */
    private static void updatepasswd(Scanner sc, Account acc) {
        System.out.println("-----------用户改密码-----------");
        while (true) {
            System.out.println("输入当前密码");
            String passwd = sc.next();

            //判断密码
            if(acc.getPasswd().equals(passwd)){
                //正确
                //输入密码
                while (true) {
                    System.out.println("输入新密码");
                    String newpasswd = sc.next();
                    System.out.println("再次输入密码");
                    String newpasswd2 = sc.next();

                    if(newpasswd.equals(newpasswd2)){
                        //两次输入一致，可以修改了
                        acc.setPasswd(newpasswd2);
                        System.out.println("修改成功");
                        return;
                    }else {
                        System.out.println("两次密码不一致");
                    }
                }
            }else {
                System.out.println("当前密码不正确");
            }
        }

    }

    /**
     * 转账
     * @param sc 扫描
     * @param acc 自己账户
     * @param accounts 全部账户集合
     */
    private static void transferMoney(Scanner sc, Account acc, ArrayList<Account> accounts) {
        System.out.println("-----------用户取钱-----------");
        // 1.分析系统是否足够两个账户
        if(accounts.size() < 2){
            System.out.println("不够两个账户，不能转账");
            return;
        }

        //判断自己账户是否有钱
        if(acc.getMoney() == 0){
            System.out.println("自己没钱");
            return;
        }

        //3.开始转账

        while (true) {
            System.out.println("输入对方卡号");
            String  cardId = sc.next();

            //卡号不能自己
            if(cardId.equals(acc.getCardid())){
                System.out.println("不能给自己转账");
                continue;//结束当此执行，进行下一次
            }

            //根据卡号查账户
            Account a = getAccountByCardId(cardId, accounts);
            if(a == null){
                System.out.println("输入账户不存在");
            }else {
                //账户存在，继续认证姓氏
                String userName = a.getUsername();
                String tip = '*' + userName.substring(1);
                System.out.println("输入" + tip + "姓氏");
                String preName = sc.next();

                //检查
                if(userName.startsWith(preName)){
                    while (true) {
                        //通过
                        //开始转账
                        System.out.println("输入转账金额");
                        double money = sc.nextDouble();
                        //判断余额
                        if(money > acc.getMoney()){
                            System.out.println("余额不足，最多转账" + acc.getMoney());
                        }else {
                            //余额足够
                            acc.setMoney(acc.getMoney() - money);
                            a.setMoney(a.getMoney() + money);
                            System.out.println("转账成功，账户剩余" + acc.getMoney());
                            return;  //转账结束，干掉方法
                        }
                    }
                }else {
                    System.out.println("输入错误");
                }
            }
        }
    }

    /**
     * 取钱
     * @param acc 当前账户对象
     * @param sc 扫描器
     */
    private static void drawMoney(Account acc, Scanner sc) {
        System.out.println("-----------用户取钱-----------");
        //判断是否有钱
        if(acc.getMoney() < 100){
            System.out.println("钱不够100，不能取");
            return;
        }
        // 提示用户输入取钱金额
        while (true) {
            System.out.println("输入取款金额");
            double money = sc.nextDouble();
            //判断金额
            if(money > acc.getQuotaMoney()){
                System.out.println("不能取，每次最多可取" + acc.getQuotaMoney());
            }else {
                //没超过限额
                // 判断是否超过总余额
                if(money > acc.getMoney()){
                    System.out.println("余额不足，还有" + acc.getMoney());
                }else {
                    //可以取钱
                    System.out.println("取钱成功" + money);
                    //跟新余额
                    acc.setMoney(acc.getMoney() - money);
                    showAccount(acc);
                    return;
                }
            }
        }
    }

    /**
     * 存钱
     * @param acc 当前账户对象
     * @param sc 扫描器
     */
    private static void depositMoney(Account acc, Scanner sc) {
        System.out.println("-----------用户存钱-----------");
        System.out.println("输入存款金额");
        double Money = sc.nextDouble();

        //跟新账户余额，原来的+存的
        acc.setMoney(acc.getMoney() + Money);
        System.out.println("存钱成功");
        showAccount(acc);

    }

    /**
     * 展示账户信息
     */
    private static void showAccount(Account acc) {
        System.out.println("--------当前账户信息如下--------------");
        System.out.println("卡号" + acc.getCardid());
        System.out.println("姓名" + acc.getUsername());
        System.out.println("余额" + acc.getMoney());
        System.out.println("限额" + acc.getQuotaMoney());
    }

    /**
     * 用户注册功能的实现
     * @param accounts，接收账户的集合
     */
    private static void register(ArrayList<Account> accounts, Scanner sc) {
        System.out.println("------------注册功能---------------");
        //创建账户对象，用于后期封装账户信息
        Account account = new Account();

        //录入当前账户的信息，注入到账户中
        System.out.println("输入用户名");
        String usereanme = sc.next();
        account.setUsername(usereanme);

        while (true) {
            System.out.println("输入密码");
            String passwd = sc.next();
            System.out.println("输入密码");
            String okpasswd = sc.next();
            if(okpasswd.equals(passwd)){
                //密码一样
                account.setPasswd(passwd);
                break;//录入成功，退出循环
            }else {
                System.out.println("两次密码不一样");
            }
        }

        System.out.println("输入账户限额");
        double quotamoney = sc.nextDouble();
        account.setQuotaMoney(quotamoney);


        //随机一个，8位不重复的号码（独立成方法）
        String cardId = getRandomCardId(accounts);
        account.setCardid(cardId);


        //把账户对象添加到账户集合中
        accounts.add(account);
        System.out.println(usereanme + "注册成功" + cardId);


    }

    /**
     * 为账户生成8位，不重复的卡号
     *
     */
    private static String getRandomCardId(ArrayList<Account> accounts) {
        Random r = new Random();
        while (true) {
            //生成八位数字
            String cardId = "";
            for (int i = 0; i < 8; i++) {
                cardId += r.nextInt(10);
            }

            //判断卡号是否重复
            //根据卡号查询账户对象
            Account acc = getAccountByCardId(cardId, accounts);
            if(acc == null){
                //cardid,没有重复，可以使用
                return cardId;//return可以立即跳出寻循环，并停止当前方法
            }
        }
    }

    /**
     * 根据卡号查询账户
     * @param cardId 卡号
     * @param accounts 全部账户集合
     * @return 账户对象
     */
    private static Account getAccountByCardId(String cardId, ArrayList<Account> accounts){
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            if(acc.getCardid().equals(cardId)){
                return acc;
            }
        }
        return null;
    }
        
}
