package edp.davinci;

public class Main {

	 public static void main(String[] args) {

	  BinaryTree tree = new BinaryTree();
	  // 添加数据测试
	  tree.insert(10);
	  tree.insert(40);
	  tree.insert(20);
	  tree.insert(3);
	  tree.insert(49);
	  tree.insert(13);
	  tree.insert(123);

	  System.out.println("root=" + tree.getRoot().getValue());
	  // 排序测试
	  tree.inOrder(tree.getRoot());
	  // 查找测试
	  if (tree.find(13) != null) {
	   System.out.println("找到了");
	  } else {
	   System.out.println("没找到");
	  }
	  // 删除测试
	/*  tree.find(40).setDelete(true);

	  if (tree.find(40) != null) {
	   System.out.println("找到了");
	  } else {
	   System.out.println("没找到");
	  }
*/
	 }

	}