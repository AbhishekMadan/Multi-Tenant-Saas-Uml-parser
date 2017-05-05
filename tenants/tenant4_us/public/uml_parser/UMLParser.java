import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class UMLParser {
	
	static String[] ClassList ;
	static String class_type;
	static String out_file;
	static ArrayList<String> java_class_name = new ArrayList<String>();
	static ArrayList<String> all_class_names = new ArrayList<String>();
	static ArrayList<String> class_names = new ArrayList<String>();
	static ArrayList<String> interface_names = new ArrayList<String>();
	static ArrayList<String> method_names = new ArrayList<String>();
	static ArrayList<String> field_names = new ArrayList<String>();
	static ArrayList<String> all_field_names = new ArrayList<String>();
	static ArrayList<String> curr_field_names = new ArrayList<String>();
	static ArrayList<String> constructor_names = new ArrayList<String>();
	static ArrayList<String> modifiers = new ArrayList<String>();
	static ArrayList<String> rel_class = new ArrayList<String>();
	static ArrayList<String> association_class = new ArrayList<String>();
	static ArrayList<String> curr_param_list = new ArrayList<String>();
	static ArrayList<String> interface_name = new ArrayList<String>();
	static ArrayList<String> curr_association = new ArrayList<String>();
	static StringBuilder sb = new StringBuilder();
	static FileInputStream in;
	static CompilationUnit cu;


	public static void main(String[] args) throws Exception{
		
		String classNames;
		String input_dir_name = args[0];
		//String input_dir_name = "E:/SJSU/202Paul/202UMLParser/SampleJavaFiles/TestCase5";
		//System.out.println("Input Path: " + input_dir_name);
		
		String outfile = args[1];
		out_file = input_dir_name + "/" + outfile +".png";
		//System.out.println("Output File:" + out_file);

		sb.append("@startuml\n");
		sb.append("skinparam classAttributeIconsize 0\n");
		
		PlantUmlGenerator classDiagramUML = new PlantUmlGenerator();
		
		//Create new file
		File f = new File(input_dir_name);
		
		//List the files in the input directory
		File[] ipFiles = f.listFiles();

		for(File fList: ipFiles)
		{
			if (fList.isFile()) {
				classNames = fList.getName();
				ClassList = classNames.split("[.]");
				if (ClassList[1].equals("java")){
					java_class_name.add(ClassList[0]);
					String filename = input_dir_name + "/" + classNames;
				
					try {
						// creates an input stream for the file to be parsed
						in = new FileInputStream(filename);
				
						// parse the file
						cu = JavaParser.parse(in);
						JavaParser.setCacheParser(false);						
						new InterfaceFirstVisitor().visit(cu,null);
					}catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						try {
							in.close();

						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
				
			}
		}
		
			for(File fList: ipFiles)
			{
				if (fList.isFile()) {
					classNames = fList.getName();
					ClassList = classNames.split("[.]");
					//System.out.println(ClassList[0]);
					if (ClassList[1].equals("java")){
						java_class_name.add(ClassList[0]);	
						String filename = input_dir_name + "/" + classNames;
					
						try {
							// creates an input stream for the file to be parsed
							in = new FileInputStream(filename);
					
							// parse the file
							cu = JavaParser.parse(in);
							//System.out.println(cu);
							JavaParser.setCacheParser(false);
							
						
						}catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							try {
								in.close();

							} catch (IOException e) {

								e.printStackTrace();
							}
						}
						//determine if its a class or interface

						//visit and print the methods names
						
						new FieldVisitor().visit(cu, null);
						new MethodVisitor().visit(cu, null);
						new ConstructorTypeVisitor().visit(cu, null);
						new ClassOrInterfaceVisitor().visit(cu,null);
						write();

					}
					
				}
			}
			for(String s: association_class){
				sb.append(s).append("\n");
		}
			sb.append("@enduml\n");
			classDiagramUML.classUML(sb);
			//System.out.println(sb);	

	}
	
	private static class InterfaceFirstVisitor extends VoidVisitorAdapter<Void>{
		public void visit(ClassOrInterfaceDeclaration c,Void arg){

			if(c.isInterface() == true){
				interface_names.add(c.getName());
			}else {
				
				all_class_names.add(c.getName());
			}
		}
	}
	
	private static class ClassOrInterfaceVisitor extends VoidVisitorAdapter<Void>{
		@Override
		public void visit(ClassOrInterfaceDeclaration c,Void arg){
			java.util.List<ClassOrInterfaceType> type;

			if (c.isInterface() == false ){
				class_names.add(c.getName());
				class_type = "class";
			}else{
				interface_name.add(c.getName());
				class_type = "interface";
			}
			
			if (c.getExtends()!= null) {
				
				type = c.getExtends();

				for(int i=0;i < type.size();i++) {
					String var = type.get(i).toString().replace("[,]","") +  "<|--" +" " +ClassList[0];
					//System.out.println(var);
					rel_class.add(var);
				}				
			}
			//System.out.println(c.getImplements());
			if (c.getImplements() != null){
				type = c.getImplements();
				//System.out.println(type);
				for(int i =0; i<type.size();i++) {
					String var = type.get(i).toString().replace("[,]","") + "<|.." + " " + ClassList[0];
					//System.out.println(var);
					rel_class.add(var);
				}
				
			}
			verifyDependency();
		
		}
	}
	
	//Fetch all fields
	private static class FieldVisitor extends VoidVisitorAdapter<Void> {
		
		public void visit(FieldDeclaration f, Void arg){

			int modifier = f.getModifiers();
			String sign = null;
			String var_f;
			String fieldType;
			ArrayList<String> curr_fieldType = new ArrayList<String>();
			
			
			if (modifier ==1){
				sign = "+";
			}else if(modifier ==2) {
				sign = "-";
			}
			
			fieldType = f.getType().toString();
			var_f = (f.getVariables().get(0).toString());

			all_field_names.add(var_f.toUpperCase());

			String text = sign + " " + var_f + " : " + fieldType;
			if ((modifier ==1 || modifier ==2) && (fieldType.contains("Collection") == false) && (java_class_name.contains(fieldType) == false)){
					field_names.add(text);
					//curr_field_names.add();
			}
			curr_fieldType.add(fieldType); 				
			for(String s : curr_fieldType){
				//System.out.println(s);
				if(java_class_name.contains(s) && s.contains("Collection") == false){
					String rev = s + " -- " + ClassList[0];
					String str = ClassList[0] + " -- " + s;
					//System.out.println("Current Association" +curr_association);
					//System.out.println("Association" + association_class);
					//System.out.println("str :"+str);
					//Add the association line only If current list do not have the reverse string and current list do not have the str
					if((curr_association.contains(rev) == false) && (curr_association.contains(str) == false)){
						association_class.add(str);
						curr_association.add(str); 
					}
				}else if(s.contains("Collection")){
					String[] param = s.split("<");
					param[1] = param[1].replace(">","");
					if(java_class_name.contains(param[1])){
						String str = ClassList[0] + " -- " + "\"*\" " + param[1];
						String star_rev = param[1] + " -- " + "\"*\" " + ClassList[0];
						String rev = param[1] + " -- " + ClassList[0];
						String without_star = ClassList[0] + " -- " + param[1];
						String both_side_stars = ClassList[0] + "\"*\" " + " -- " + "\"*\" " + param[1];
						//System.out.println("Current Association in collection loop" +curr_association);
						//System.out.println("Association in collection loop" + association_class);
						//System.out.println("str in collection loop :"+str);
						//System.out.println("star _rev "+ star_rev);
						
						if((curr_association.contains(star_rev) == false) && (curr_association.contains(str) == false) && (curr_association.contains(without_star) == false) && (curr_association.contains(rev) == false)){
							association_class.add(str);
							curr_association.add(str);
							curr_association.add(without_star);			
						}else if (curr_association.contains(star_rev) ){
							association_class.add(both_side_stars);
							curr_association.add(both_side_stars);
							association_class.remove(str);
							association_class.remove(star_rev);
						}
					}
				}
			}
		}
		
	}
	
	//Fetch all methods in the class
	private static class MethodVisitor extends VoidVisitorAdapter<Void>{
		@Override
		public void visit(MethodDeclaration m,Void arg){

			int modifier = m.getModifiers();
			String[] splitVariable;
			String[] part;
			int flag = 0;
			String text = " ";
			String tempString;
			String mergeVariable;
	
				
		if ((m.getName().substring(0,3).equals("get") || m.getName().substring(0,3).equals("set")) && (modifier ==1) && (all_field_names.contains(m.getName().substring(3).toUpperCase()))) 
		{
			flag = 1;
			
			for(int i=0;i<=field_names.size()-1;i++){

				splitVariable = field_names.get(i).split(":");
				part = splitVariable[0].split(" ");
				String fieldName = part[1];

				if (m.getName().substring(3).equalsIgnoreCase(fieldName)) {
					flag = 1;
					tempString = field_names.get(i).substring(1);
					mergeVariable = "+" + tempString;
					field_names.set(i, mergeVariable);
				}
			}
		}else if (((modifier == 1) || (modifier ==1025) || (modifier == 9)) && (flag ==0)){

				//System.out.println("in flag = 0 "+ m.getName());
				if (m.getParameters() != null){
					curr_param_list.add(m.getParameters().toString().replace("[","").replace("]",""));
					
					StringBuffer  str = new StringBuffer();
					for(int i = 0; i< m.getParameters().size() ; i++){
						 str.append(m.getParameters().get(i).getId() + " : " + m.getParameters().get(i).getType() + " ");
					}
					if (modifier == 9){
						text = "{static}" + m.getName() + "(" + str +")" + ":" + m.getType();
						method_names.add(text);
					}else {
						text = m.getName() + "(" + str +")" + ":" + m.getType();
						method_names.add(text);
					}
					
					if (m.getName().contains("main")) {
						//System.out.println("Body of main *" +m.getBody().getStmts().get(0).toString());
						for(int i=0;i<+m.getBody().getStmts().size()-1;i++){
							splitVariable = m.getBody().getStmts().get(i).toString().split(" ");
							//System.out.println("split variable"+splitVariable[0]);
							if(interface_names.contains(splitVariable[0]) == true){
								text = ClassList[0] + "..>" + splitVariable[i];
								rel_class.add(text);
							}
						}
					}
				}
				else if(m.getParameters() == null){
					text = m.getName() + "()" + ":" + m.getType();
					method_names.add(text);
				}
			}
		flag = 0;
		}
	}
	
	//Fetch all Constructors
	private static class ConstructorTypeVisitor extends VoidVisitorAdapter<Void> {
		
		public void visit(ConstructorDeclaration cd, Void arg){
						
			if (cd.getParameters() != null){
				curr_param_list.add(cd.getParameters().toString().replace("["," ").replace("]", " "));
				
				StringBuffer  str = new StringBuffer();
				for(int i = 0; i< cd.getParameters().size() ; i++){
					 str.append(cd.getParameters().get(i).getId() + " : " + cd.getParameters().get(i).getType() + " ");
				}
				String text = cd.getName() + "(" + str + ")";
				constructor_names.add(text);

			}else if (cd.getParameters() == null){
				String str = cd.getName() + "()";
				constructor_names.add(str);
			}
			constructorDependency();
		}
	}
	
	public static void verifyDependency() {
		String[] param;
		String text;


		for(String p: curr_param_list){
			//System.out.println("curr params list-->" +p);
			param = p.trim().split(" ");
			if(interface_names.contains(param[0]) && interface_names.contains(ClassList[0]) == false){
				text = ClassList[0] + "..>" + param[0] + ":uses";
				if(rel_class.contains(text) == false){
					rel_class.add(text);
				}
			}			
		}			
		curr_param_list.clear();
	}
	
	public static void constructorDependency() {
		String[] paramC;
		String text;
							
		for(String p: curr_param_list){
			//System.out.println("curr params list-->" +p);
			paramC = p.trim().split(" ");
			if(interface_names.contains(paramC[0]) && interface_names.contains(ClassList[0]) == false){
				text = ClassList[0] + "..>" + paramC[0];
				if(rel_class.contains(text) == false){
					rel_class.add(text);
				}
			}					
		}
	curr_param_list.clear();
	}
	
	
private static void write(){
	try {
		//sb.setLength(0);
		
		if (class_type == "interface") {
			sb.append(class_type + " " + ClassList[0] + "<<interface>>" + "{" + "\n");
		} else {
			sb.append(class_type + " " + ClassList[0] + "{" + "\n");
		}
		
		for(String s: constructor_names) {
			sb.append("+").append(s).append("\n");
		}
		for(String s: method_names) {
			//System.out.println(s);
			sb.append("+").append(s).append("\n");
		}
		for (String s: field_names){
			sb.append(s).append("\n");
		}
		sb.append("}").append("\n");
		for(String s: rel_class){			
			//System.out.println(s);			
			sb.append(s).append("\n");
		}
//		for(String s: association_class){
//				sb.append(s).append("\n");
//		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	constructor_names.clear();
	method_names.clear();
	field_names.clear();
	rel_class.clear();
//	association_class.clear();
	
	}
}


