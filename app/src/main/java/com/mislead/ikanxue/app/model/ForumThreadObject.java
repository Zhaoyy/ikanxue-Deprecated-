package com.mislead.ikanxue.app.model;

import java.util.List;

/**
 * ForumThreadObject
 *
 * @author Mislead
 *         DATE: 2015/7/11
 *         DESC:
 **/
public class ForumThreadObject {

  private static String TAG = "ForumThreadObject";
  /**
   * postbits : [{"posttime":"14:49:51","thumbnail":0,"postdate":"2015-07-13","avatardateline":"1421890114","postid":"1380994","avatar":"1","message":"今天去一家面试，让我通过dump文件。之前没有分析过dump文件，想问问一些问题，这家公司的给了我一个可执行程序，按按钮会崩溃，让我崩溃时用windbg连上去dump,然后根据dump文件分析找到错误的代码行。可是我发现崩溃时windows读条，我附加到进程上.dump&nbsp;/f&nbsp;生存dmp文件，再用windbg打开，调用堆栈和崩溃时的调用堆栈式不一样的，而是像下面一样<br
   * />\r\n<br />\r\nChildEBP&nbsp;RetAddr&nbsp;&nbsp;<br />\r\n00bffac4&nbsp;771a10d9&nbsp;ntdll!DbgBreakPoint<br
   * />\r\n00bffaf4&nbsp;76917c04&nbsp;ntdll!DbgUiRemoteBreakin+0x39<br
   * />\r\n00bffb08&nbsp;7714ad1f&nbsp;kernel32!BaseThreadInitThunk+0x24<br
   * />\r\n00bffb50&nbsp;7714acea&nbsp;ntdll!__RtlUserThreadStart+0x2f<br
   * />\r\n00bffb60&nbsp;00000000&nbsp;ntdll!_RtlUserThreadStart+0x1b<br />\r\n<br
   * />\r\n，实际上崩溃的原因应该是某个函数内对一个int*&nbsp;a=0;*a=0;这样的对空指针赋值。我想我dump的时机是否正确，或者通过这样的dump文件怎么追溯错误的地方","userid":643217,"username":"CptPrice"},{"posttime":"16:14:18","thumbnail":0,"postdate":"2015-07-13","avatardateline":"1322011835","postid":"1381022","avatar":"1","message":"ntdll!DbgUiRemoteBreakin+0x39这个是windbg的attach机制生成的break&nbsp;point线程。不是问题线程<br
   * />\n你切换下吧。可以用~*k显示所有线程栈。。找到问题线程后，如ID是5,就用~5s切换过去。done.<br />\n<br
   * />\nps：要是搞这块的话，估计还有好多要了解，做好心理准备","userid":363678,"username":"zhouws"}]
   * time : 1436775707
   * pagenav : 0
   */
  private List<PostbitsEntity> postbits;
  private int time;
  private int pagenav;

  public void setPostbits(List<PostbitsEntity> postbits) {
    this.postbits = postbits;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void setPagenav(int pagenav) {
    this.pagenav = pagenav;
  }

  public List<PostbitsEntity> getPostbits() {
    return postbits;
  }

  public int getTime() {
    return time;
  }

  public int getPagenav() {
    return pagenav;
  }

  public class PostbitsEntity {
    private int postid;
    private int thumbnail;
    private String username;
    private int userid;
    private int avatar;
    private long avatardateline;
    private String posttime;
    private String postdate;

    private String message;
    private String title;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public int getPostid() {
      return postid;
    }

    public void setPostid(int postid) {
      this.postid = postid;
    }

    public int getThumbnail() {
      return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
      this.thumbnail = thumbnail;
    }

    public int getUserid() {
      return userid;
    }

    public void setUserid(int userid) {
      this.userid = userid;
    }

    public int getAvatar() {
      return avatar;
    }

    public void setAvatar(int avatar) {
      this.avatar = avatar;
    }

    public long getAvatardateline() {
      return avatardateline;
    }

    public void setAvatardateline(long avatardateline) {
      this.avatardateline = avatardateline;
    }

    public String getPosttime() {
      return posttime;
    }

    public void setPosttime(String posttime) {
      this.posttime = posttime;
    }

    public String getPostdate() {
      return postdate;
    }

    public void setPostdate(String postdate) {
      this.postdate = postdate;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
  }
}
