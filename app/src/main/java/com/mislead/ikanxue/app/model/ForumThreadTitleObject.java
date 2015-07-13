package com.mislead.ikanxue.app.model;

import java.util.List;

/**
 * ForumThreadTitleObject
 *
 * @author Mislead
 *         DATE: 2015/7/13
 *         DESC:
 **/
public class ForumThreadTitleObject {

  private static String TAG = "ForumThreadTitleObject";
  /**
   * threadList : [{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":202299,"threadtitle":"OD有什么断点可以对游戏绘画禁止的么","postuserid":101992,"sticky":0,"lastpostdate":"2015-07-10","postusername":"jiangyi","views":139,"open":1,"replycount":2},{"avatardateline":"1432270919","goodnees":0,"avatar":1,"globalsticky":0,"threadid":201668,"threadtitle":"不知道未来的路在何方！！！","postuserid":674668,"sticky":0,"lastpostdate":"2015-07-10","postusername":"我是谁！","views":2604,"open":1,"replycount":40},{"avatardateline":"1417064309","goodnees":0,"avatar":1,"globalsticky":0,"threadid":202298,"threadtitle":"静态对象初始化时机","postuserid":658048,"sticky":0,"lastpostdate":"2015-07-10","postusername":"tiji","views":72,"open":1,"replycount":0},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":202185,"threadtitle":"请教看雪大大们，关于HOOK收包Call写法问题","postuserid":214594,"sticky":0,"lastpostdate":"2015-07-10","postusername":"laosanls","views":364,"open":1,"replycount":3},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":201628,"threadtitle":"小弟初学破解，脱完壳程序不能运行，求修复方法","postuserid":94733,"sticky":0,"lastpostdate":"2015-07-10","postusername":"coolpzk","views":364,"open":1,"replycount":5},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":202288,"threadtitle":"找老师帮忙剥离NP检测","postuserid":436675,"sticky":0,"lastpostdate":"2015-07-10","postusername":"skyhell","views":218,"open":1,"replycount":2},{"avatardateline":"1273453736","goodnees":0,"avatar":1,"globalsticky":0,"threadid":163725,"threadtitle":"如何让电脑无法启动","postuserid":109523,"sticky":0,"lastpostdate":"2015-07-10","postusername":"winnip","views":356377,"open":1,"replycount":1092},{"avatardateline":"1376161365","goodnees":0,"avatar":1,"globalsticky":0,"threadid":202274,"threadtitle":"xp不支持usb3.0吗？","postuserid":592857,"sticky":0,"lastpostdate":"2015-07-10","postusername":"稀释回忆","views":281,"open":1,"replycount":5},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":202004,"threadtitle":"win7
   * x86 内核文件修改","postuserid":359816,"sticky":0,"lastpostdate":"2015-07-10","postusername":"wusha","views":556,"open":1,"replycount":8},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":202239,"threadtitle":"各位大神，帮看一下这个软件是什么壳，","postuserid":184221,"sticky":0,"lastpostdate":"2015-07-10","postusername":"ctrlandn","views":272,"open":1,"replycount":3},{"avatardateline":"1429150109","goodnees":0,"avatar":1,"globalsticky":0,"threadid":202194,"threadtitle":"某游戏崩溃现场，求指点","postuserid":640373,"sticky":0,"lastpostdate":"2015-07-10","postusername":"大p","views":751,"open":1,"replycount":7},{"avatardateline":"1421890114","goodnees":0,"avatar":1,"globalsticky":0,"threadid":202246,"threadtitle":"win8.1+wdk8.1+win8.1虚拟机搭建驱动调试环境求指导","postuserid":643217,"sticky":0,"lastpostdate":"2015-07-09","postusername":"CptPrice","views":301,"open":1,"replycount":3},{"avatardateline":"1427116687","goodnees":0,"avatar":1,"globalsticky":0,"threadid":202226,"threadtitle":"【求助】Mysql与JSP","postuserid":672709,"sticky":0,"lastpostdate":"2015-07-09","postusername":"Nanar","views":128,"open":1,"replycount":1},{"avatardateline":"1414758812","goodnees":0,"avatar":1,"globalsticky":0,"threadid":201893,"threadtitle":"网络验证的一个exe","postuserid":655090,"sticky":0,"lastpostdate":"2015-07-09","postusername":"gejiyu","views":681,"open":1,"replycount":5},{"avatardateline":"0","goodnees":0,"avatar":0,"globalsticky":0,"threadid":197403,"threadtitle":"【求助】请问这是用了什么混淆算法？","postuserid":179306,"sticky":0,"lastpostdate":"2015-07-09","postusername":"gapple","views":1388,"open":1,"replycount":5}]
   * time : 1436750480
   * pagenav : 1458
   */
  private List<ThreadListEntity> threadList;
  private long time;
  private int pagenav;

  public void setThreadList(List<ThreadListEntity> threadList) {
    this.threadList = threadList;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public void setPagenav(int pagenav) {
    this.pagenav = pagenav;
  }

  public List<ThreadListEntity> getThreadList() {
    return threadList;
  }

  public long getTime() {
    return time;
  }

  public int getPagenav() {
    return pagenav;
  }

  public class ThreadListEntity {
    /**
     * avatardateline : 0
     * goodnees : 0
     * avatar : 0
     * globalsticky : 0
     * threadid : 202299
     * threadtitle : OD有什么断点可以对游戏绘画禁止的么
     * postuserid : 101992
     * sticky : 0
     * lastpostdate : 2015-07-10
     * postusername : jiangyi
     * views : 139
     * open : 1
     * replycount : 2
     */
    private String avatardateline;
    private int goodnees;
    private int avatar;
    private int globalsticky;
    private int threadid;
    private String threadtitle;
    private int postuserid;
    private int sticky;
    private String lastpostdate;
    private String postusername;
    private int views;
    private int open;
    private int replycount;

    public void setAvatardateline(String avatardateline) {
      this.avatardateline = avatardateline;
    }

    public void setGoodnees(int goodnees) {
      this.goodnees = goodnees;
    }

    public void setAvatar(int avatar) {
      this.avatar = avatar;
    }

    public void setGlobalsticky(int globalsticky) {
      this.globalsticky = globalsticky;
    }

    public void setThreadid(int threadid) {
      this.threadid = threadid;
    }

    public void setThreadtitle(String threadtitle) {
      this.threadtitle = threadtitle;
    }

    public void setPostuserid(int postuserid) {
      this.postuserid = postuserid;
    }

    public void setSticky(int sticky) {
      this.sticky = sticky;
    }

    public void setLastpostdate(String lastpostdate) {
      this.lastpostdate = lastpostdate;
    }

    public void setPostusername(String postusername) {
      this.postusername = postusername;
    }

    public void setViews(int views) {
      this.views = views;
    }

    public void setOpen(int open) {
      this.open = open;
    }

    public void setReplycount(int replycount) {
      this.replycount = replycount;
    }

    public String getAvatardateline() {
      return avatardateline;
    }

    public int getGoodnees() {
      return goodnees;
    }

    public int getAvatar() {
      return avatar;
    }

    public int getGlobalsticky() {
      return globalsticky;
    }

    public int getThreadid() {
      return threadid;
    }

    public String getThreadtitle() {
      return threadtitle;
    }

    public int getPostuserid() {
      return postuserid;
    }

    public int getSticky() {
      return sticky;
    }

    public String getLastpostdate() {
      return lastpostdate;
    }

    public String getPostusername() {
      return postusername;
    }

    public int getViews() {
      return views;
    }

    public int getOpen() {
      return open;
    }

    public int getReplycount() {
      return replycount;
    }
  }
}
