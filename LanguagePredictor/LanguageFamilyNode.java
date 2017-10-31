import java.util.ArrayList;

public class LanguageFamilyNode
{
  //private variables for the LanguageFamilyNode objects
  private String name;
  private ArrayList<LanguageFamilyNode> children;
  
  //constructor for the Objects
  public LanguageFamilyNode (String name)
  {
    this.name=name;
    //declaring that the ArrayList children is an ArrayList ofLanguageFamilyNode objects
    children = new ArrayList<LanguageFamilyNode>();
  }
  
  public void setChildren(LanguageFamilyNode child)
  {
    //adds the LanguageFamilyNode object to the children ArrayList
    children.add(child);
  }
  
  //simple getter methods for the name and children
  public String getName()
  {
    return name;
  }
  
  public ArrayList<LanguageFamilyNode> getChildren()
  {
    return children;
  }
  
  //recursive print method
  public void printAllDescendants(String s)
  {
    //want to print out the starting name
    System.out.println(this.getName());
    //this will increase the indentation each time the printAllDescendants method is called recursively
    s+="  ";
    
    ArrayList<LanguageFamilyNode> children = this.getChildren();
    //recursively call the printAllDescendants method for all of the descendants of the parent language
    for(int i=0; i<children.size(); i++)
    {
      System.out.print(s);
      children.get(i).printAllDescendants(s);
    }
  }
  
  //this is the facade method that doesn't need to take in any parameters
  public void printAllDescendants()
  {
    //the first String prefix is just an empty string i.e. no indentation
    //the indentation is increased within the real printAllDescendants method
    this.printAllDescendants("");
  }
  
  
  public static void main(String[] args)
  {
    //creating all the LanguageFamilyNode objects
    LanguageFamilyNode germLang = new LanguageFamilyNode("Germanic");
    LanguageFamilyNode wGerm = new LanguageFamilyNode("West Germanic");
    LanguageFamilyNode hGerm = new LanguageFamilyNode("High German");
    LanguageFamilyNode lGerm = new LanguageFamilyNode("Low German");
    LanguageFamilyNode angloFri = new LanguageFamilyNode("Anglo-Frisian");
    LanguageFamilyNode yiddish = new LanguageFamilyNode("Yiddish");
    LanguageFamilyNode german = new LanguageFamilyNode("German");
    LanguageFamilyNode plattdeutsch = new LanguageFamilyNode("Plattdeutsch");
    LanguageFamilyNode netherlandish = new LanguageFamilyNode("Netherlandish");
    LanguageFamilyNode afrikaans = new LanguageFamilyNode("Afrikaans");
    LanguageFamilyNode frisian = new LanguageFamilyNode("Frisian");
    LanguageFamilyNode english = new LanguageFamilyNode("English");
    LanguageFamilyNode nGerm = new LanguageFamilyNode("North Germanic");
    LanguageFamilyNode faroese = new LanguageFamilyNode("Faroese");
    LanguageFamilyNode norwegian = new LanguageFamilyNode("Norwegian");
    LanguageFamilyNode icelandic = new LanguageFamilyNode("Icelandic");
    LanguageFamilyNode swedish = new LanguageFamilyNode("Swedish");
    LanguageFamilyNode danish = new LanguageFamilyNode("Danish");
    LanguageFamilyNode eGerm = new LanguageFamilyNode("East Germanic");
    LanguageFamilyNode gothic = new LanguageFamilyNode("Gothic");
    LanguageFamilyNode vandalic = new LanguageFamilyNode("Vandalic");
    LanguageFamilyNode burgundian = new LanguageFamilyNode("Burgundian");
    LanguageFamilyNode langobardic = new LanguageFamilyNode("Langobardic");
    
    //setting the appropriate children for each node
    germLang.setChildren(eGerm);
    germLang.setChildren(nGerm);
    germLang.setChildren(wGerm);
    eGerm.setChildren(gothic);
    eGerm.setChildren(vandalic);
    eGerm.setChildren(burgundian);
    eGerm.setChildren(langobardic);
    nGerm.setChildren(icelandic);
    nGerm.setChildren(faroese);
    nGerm.setChildren(norwegian);
    nGerm.setChildren(danish);
    nGerm.setChildren(swedish);
    wGerm.setChildren(hGerm);
    wGerm.setChildren(lGerm);
    wGerm.setChildren(angloFri);
    hGerm.setChildren(yiddish);
    hGerm.setChildren(german);
    lGerm.setChildren(plattdeutsch);
    lGerm.setChildren(netherlandish);
    lGerm.setChildren(afrikaans);
    angloFri.setChildren(frisian);
    angloFri.setChildren(english);
    
    
    germLang.printAllDescendants();
    
  }
}

