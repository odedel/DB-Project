package triviaDB;

import triviaDB.GUIUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Timer ;



import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class GUITrivia 
{

	private static final int MAX_ERRORS = 3;
	private Shell shell;
	private Label scoreLabel;
	private Composite questionPanel;
	private Composite signInPanel;
	private Composite headLine;
	private Font boldFont;
	private String lastAnswer = "";
	private boolean soloGame;
	private String Player1UserName ="";
	private String Player2UserName ="";
	private int wrongAnswer = 0;
	private int totalPoint = 0;
	private int QuesCounter = 0;
	private Timer timer;
	public void open()
	{
		createShell();
		runApplication();
	}

	/**
	 * Creates the widgets of the application main window
	 */
	private void createShell() 
	{
		Display display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("Trivia");

		// window style
		Rectangle monitor_bounds = shell.getMonitor().getBounds();
		shell.setSize(new Point(monitor_bounds.width/2 ,	monitor_bounds.height/2 ));
		
		shell.setLayout(new GridLayout());

		FontData fontData = new FontData();
		fontData.setStyle(SWT.BOLD);
		boldFont = new Font(shell.getDisplay(), fontData);
		
		//create window panels
		createMainScreen();
		//createScorePanel();
		//createQuestionPanel();
	}

	/**
	 * Creates the widgets of the form for trivia file selection
	 */
	private void createMainScreen() 
	{

		
		
		/*final Composite fileSelection = new Composite(shell, SWT.NULL);
		fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
		fileSelection.setLayout(new GridLayout(2, false));*/
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		Label label3 = new Label(headLine, SWT.CENTER| SWT.WRAP);
		label3.setText("Welcome to Trivia\n\n\n\n\n");
		label3.setLayoutData(GUIUtils.createFillGridData(1));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(4, true));
		
		GridData signInLayoutData = GUIUtils.createFillGridData(1);
		signInLayoutData.horizontalAlignment = SWT.CENTER;
		signInLayoutData.widthHint = 150;
		
		

		
		Label label1 = new Label(signInPanel, SWT.CENTER| SWT.WRAP);
		label1.setLayoutData(GUIUtils.createFillGridData(1));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));

		Label label2 = new Label(questionPanel, SWT.CENTER);
		label2.setLayoutData(GUIUtils.createFillGridData(2));
		
		
		GridData answerLayoutData = GUIUtils.createFillGridData(2);
		answerLayoutData.verticalIndent  = 8;
		answerLayoutData.horizontalAlignment = SWT.CENTER;
		answerLayoutData.widthHint = 120;
		
		//Player1UserName = "TamirAviv";
		
		if(Player1UserName=="")
		{
			final Button player1SignInButton = new Button(signInPanel, SWT.PUSH );
			player1SignInButton.setText("player 1 - SignIn");
			player1SignInButton.setLayoutData(signInLayoutData);
			player1SignInButton.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					createSignInScreen();
				}
			});
		}
		else
		{
			final Text userName = new Text(signInPanel, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
			userName.setText("Welcome " + Player1UserName);
			userName.setLayoutData(signInLayoutData);
			
		}
		
		//Player2UserName ="YaronLibman";
		
		if(Player2UserName=="")
		{
			final Button player2SignInButton = new Button(signInPanel, SWT.PUSH);
			player2SignInButton.setText("player 2 - SignIn");
			player2SignInButton.setLayoutData(signInLayoutData);
			player2SignInButton.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					createSignInScreen();
				}
			});
		}
		else
		{
			final Text userName2 = new Text(signInPanel, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
			userName2.setText("Welcome " + Player2UserName);
			userName2.setLayoutData(signInLayoutData);
		}
		
		final Button updateDBButton = new Button(questionPanel, SWT.PUSH);
		updateDBButton.setText("updateDB");		
		updateDBButton.setLayoutData(answerLayoutData);
		
		final Button startGameButton = new Button(questionPanel, SWT.PUSH);
		startGameButton.setText("Start Game");		
		startGameButton.setLayoutData(answerLayoutData);
		startGameButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if(Player1UserName=="" && Player2UserName=="")
				{
					//we need to print a messeage
				}
				else if(Player1UserName!="" && Player2UserName!="")
				{
					createDuoGameScreen();
				}
				else
				{
					createSoloGameScreen();
				}
			}
		});
		
		final Button statisticButton = new Button(questionPanel,SWT.PUSH);
		statisticButton.setText("statistic");		
		statisticButton.setLayoutData(answerLayoutData);
		statisticButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				createStatisticScreen();
			}
		});
		
		
		final Button signUpButton = new Button(questionPanel,SWT.PUSH);
		signUpButton.setText("Sign Up");		
		signUpButton.setLayoutData(answerLayoutData);
		signUpButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				createSignUpScreen();
			}
		});
		
		final Button QuitButton = new Button(questionPanel,SWT.PUSH);
		QuitButton.setText("Quit");		
		QuitButton.setLayoutData(answerLayoutData);
		QuitButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				shell.dispose();
			}
		});
		
		questionPanel.pack();
		signInPanel.pack();
		headLine.pack();
		shell.layout();
	}
	
	private void createSignInScreen() 
	{
		
		Control[] children = headLine.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Control[] children2 = signInPanel.getChildren();
		for (Control control : children2)
		{
			control.dispose();
		}
		
		Control[] children3 = questionPanel.getChildren();
		for (Control control : children3) 
		{
			control.dispose();
		}
		
		headLine.dispose();
		signInPanel.dispose();
		questionPanel.dispose();
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(4, true));

				
		Label label = new Label(headLine, SWT.CENTER);
		label.setText("Login to your account\n\n\n\n\n\n\n\n\n\n\n");
		label.setLayoutData(GUIUtils.createFillGridData(2));
		
		GridData tempLayoutData = GUIUtils.createFillGridData(2);
		tempLayoutData.verticalIndent  = 5;
		tempLayoutData.horizontalAlignment = SWT.CENTER;
		
		GridData tempLayoutData2 = GUIUtils.createFillGridData(2);
		tempLayoutData2.verticalIndent  = 5;
		tempLayoutData2.horizontalAlignment = SWT.CENTER;
		tempLayoutData2.widthHint = 150;
		
		final Text userName = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
		userName.setText("userName");
		userName.setLayoutData(tempLayoutData2);
		
		
		final Text passWord = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
		passWord.setText("passWord");
		passWord.setLayoutData(tempLayoutData2);
		
		final Button LoginButton = new Button(headLine, SWT.PUSH);
		LoginButton.setText("Login");		
		LoginButton.setLayoutData(tempLayoutData);
		
		
		final Button backButton = new Button(questionPanel, SWT.PUSH);
		backButton.setText("back");		
		backButton.setLayoutData(tempLayoutData);
		backButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Control[] children = headLine.getChildren();
				for (Control control : children) {
					control.dispose();
				}
				
				Control[] children2 = signInPanel.getChildren();
				for (Control control : children2)
				{
					control.dispose();
				}
				
				Control[] children3 = questionPanel.getChildren();
				
				for (Control control : children3) 
				{
					control.dispose();
				}
				
				headLine.dispose();
				signInPanel.dispose();
				questionPanel.dispose();
				
				createMainScreen();
			}
		});
	
		questionPanel.getParent().layout();
			
		
	}
	
	private void createSignUpScreen() 
	{
		Control[] children = headLine.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Control[] children2 = signInPanel.getChildren();
		for (Control control : children2)
		{
			control.dispose();
		}
		
		Control[] children3 = questionPanel.getChildren();
		
		for (Control control : children3) 
		{
			control.dispose();
		}
		
		headLine.dispose();
		signInPanel.dispose();
		questionPanel.dispose();
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(4, true));
		
		
		/*final Composite fileSelection = new Composite(shell, SWT.NULL);
		fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
		fileSelection.setLayout(new GridLayout(2, false));*/
				
		
		Label label = new Label(headLine, SWT.CENTER);
		label.setText("Create your account\n\n\n\n\n\n\n");
		label.setLayoutData(GUIUtils.createFillGridData(2));
		
		GridData tempLayoutData = GUIUtils.createFillGridData(2);
		tempLayoutData.verticalIndent  = 5;
		tempLayoutData.horizontalAlignment = SWT.CENTER;
		tempLayoutData.widthHint = 100;
		
		GridData tempLayoutData2 = GUIUtils.createFillGridData(2);
		tempLayoutData2.verticalIndent  = 5;
		tempLayoutData2.horizontalAlignment = SWT.CENTER;
		tempLayoutData2.widthHint = 150;
		
		final Text filePathField = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
		filePathField.setText("userName");
		filePathField.setLayoutData(tempLayoutData2);
		
		
		final Text filePathField2 = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
		filePathField2.setText("password");
		filePathField2.setLayoutData(tempLayoutData2);
		
		final Text filePathField3 = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
		filePathField3.setText("verify password");
		filePathField3.setLayoutData(tempLayoutData2);
		
		final Button updateDBButton = new Button(headLine, SWT.PUSH);
		updateDBButton.setText("Sign Up");		
		updateDBButton.setLayoutData(tempLayoutData);
		
		
		final Button backButton = new Button(questionPanel, SWT.PUSH);
		backButton.setText("back");		
		backButton.setLayoutData(tempLayoutData);
		backButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Control[] children = headLine.getChildren();
				for (Control control : children) {
					control.dispose();
				}
				
				Control[] children2 = signInPanel.getChildren();
				for (Control control : children2)
				{
					control.dispose();
				}
				
				Control[] children3 = questionPanel.getChildren();
				
				for (Control control : children3) 
				{
					control.dispose();
				}
				
				headLine.dispose();
				signInPanel.dispose();
				questionPanel.dispose();
				
				createMainScreen();
			}
		});
		
		questionPanel.getParent().layout();
	}

	private void createStatisticScreen() 
	{
		Control[] children = headLine.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Control[] children2 = signInPanel.getChildren();
		for (Control control : children2)
		{
			control.dispose();
		}
		
		Control[] children3 = questionPanel.getChildren();
		
		for (Control control : children3) 
		{
			control.dispose();
		}
		
		headLine.dispose();
		signInPanel.dispose();
		questionPanel.dispose();
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(2, true));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));
		
		
		GridData tempLayoutData = GUIUtils.createFillGridData(2);
		tempLayoutData.verticalIndent  = 5;
		tempLayoutData.horizontalAlignment = SWT.CENTER;
		
		GridData tempLayoutData2 = GUIUtils.createFillGridData(2);
		tempLayoutData2.horizontalAlignment = SWT.CENTER;
		
		
		Table table = new Table(signInPanel, SWT.BORDER |SWT.CENTER | SWT.FILL);
	    
	    String[] titles = { "userName","Score","GameType" };

	    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) 
	    {
	      TableColumn column = new TableColumn(table, SWT.CENTER|SWT.FILL );
	      column.setText(titles[loopIndex]);
	    }
	    
	    table.setHeaderVisible(true);

	    
	    for (int loopIndex = 0; loopIndex < 5; loopIndex++)
	    {
	        TableItem item = new TableItem(table, SWT.FILL);
	        item.setText( new String[] {"TamirAviv","100" ,"1 vs 1"});
	        

	    }
	    
	    for (int loopIndex = 0; loopIndex < 5; loopIndex++)
	    {
	        TableItem item = new TableItem(table, SWT.FILL);
	        item.setText( new String[] {"YaronHaGever","110" ,"solo"});
	        

	    }
	    
	    for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) 
	    {
	        table.getColumn(loopIndex).pack();
	    }
	    
	    //table.setBounds(25, 25, 220, 200);
	    

	    table.setLayoutData(tempLayoutData2);
	    
	    
		Label label3 = new Label(headLine, SWT.CENTER| SWT.WRAP);
		label3.setText("Welcome to The Hell Of Fame\n\n");
		label3.setLayoutData(GUIUtils.createFillGridData(1));
		
		/*Label label2 = new Label(signInPanel, SWT.CENTER| SWT.WRAP | SWT.BORDER);
		label2.setLayoutData(GUIUtils.createFillGridData(2));*/
		
		
		
		
		final Button backButton = new Button(questionPanel, SWT.PUSH);
		backButton.setText("back");		
		backButton.setLayoutData(tempLayoutData);
		backButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Control[] children = headLine.getChildren();
				for (Control control : children) {
					control.dispose();
				}
				
				Control[] children2 = signInPanel.getChildren();
				for (Control control : children2)
				{
					control.dispose();
				}
				
				Control[] children3 = questionPanel.getChildren();
				
				for (Control control : children3) 
				{
					control.dispose();
				}
				
				headLine.dispose();
				signInPanel.dispose();
				questionPanel.dispose();
				
				createMainScreen();
			}
		});
		
		questionPanel.getParent().layout();
	}
	
	private void createSoloGameScreen() 
	{
		Control[] children = headLine.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Control[] children2 = signInPanel.getChildren();
		for (Control control : children2)
		{
			control.dispose();
		}
		
		Control[] children3 = questionPanel.getChildren();
		
		for (Control control : children3) 
		{
			control.dispose();
		}
		
		headLine.dispose();
		signInPanel.dispose();
		questionPanel.dispose();
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(2, true));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));
		
		Label label = new Label(headLine, SWT.CENTER);
		label.setText("Ready? Set.... GO !\n\n\n\n\n\n\n");
		label.setLayoutData(GUIUtils.createFillGridData(2));
		
		
		Label label2 = new Label(signInPanel, SWT.CENTER| SWT.WRAP);
		label2.setLayoutData(GUIUtils.createFillGridData(1));
		
		GridData tempLayoutData = GUIUtils.createFillGridData(2);
		tempLayoutData.verticalIndent  = 5;
		tempLayoutData.horizontalAlignment = SWT.CENTER;
		tempLayoutData.widthHint = 100;
		
		timer = new Timer(10, new ActionListener() {
	          public void actionPerformed(ActionEvent e) 
	          {
	        	  if(timer.isRunning())
	        	  {
	        		  timer.stop();
	        		  
	        	  }
	        	  else
	        	  {
	        		  timer.start();
	        		    try {
	        		      Thread.sleep(1000000);
	        		    } 
	        		    catch (InterruptedException e2) {
	        		    }
	        		    
	        		    final Text filePathField = new Text(headLine, SWT.SINGLE| SWT.BORDER|SWT.CENTER);
	        			filePathField.setText("userName");
	        			filePathField.setLayoutData(tempLayoutData);
	        	  }
	          }
	       });
	  
		    
		final Button quitButton = new Button(questionPanel, SWT.PUSH);
		quitButton.setText("Quit");		
		quitButton.setLayoutData(tempLayoutData);
		quitButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Control[] children = headLine.getChildren();
				for (Control control : children) {
					control.dispose();
				}
				
				Control[] children2 = signInPanel.getChildren();
				for (Control control : children2)
				{
					control.dispose();
				}
				
				Control[] children3 = questionPanel.getChildren();
				
				for (Control control : children3) 
				{
					control.dispose();
				}
				
				headLine.dispose();
				signInPanel.dispose();
				questionPanel.dispose();
				
				createMainScreen();
			}
		});
		
		questionPanel.getParent().layout();
		
	}
	
	private void createDuoGameScreen() 
	{
		Control[] children = headLine.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Control[] children2 = signInPanel.getChildren();
		for (Control control : children2)
		{
			control.dispose();
		}
		
		Control[] children3 = questionPanel.getChildren();
		
		for (Control control : children3) 
		{
			control.dispose();
		}
		
		headLine.dispose();
		signInPanel.dispose();
		questionPanel.dispose();
		
		headLine = new Composite(shell, SWT.NONE);
		headLine.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		headLine.setLayout(new GridLayout(1, true));
		
		signInPanel = new Composite(shell, SWT.CENTER);
		signInPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		signInPanel.setLayout(new GridLayout(2, true));
		
		questionPanel = new Composite(shell, SWT.NONE);
		questionPanel.setLayoutData(new GridData(GridData.FILL,	GridData.FILL, true, true));
		questionPanel.setLayout(new GridLayout(2, true));
		
		Label label = new Label(headLine, SWT.CENTER);
		label.setText("Ready? Set.... GO !\n\n\n\n\n\n\n");
		label.setLayoutData(GUIUtils.createFillGridData(2));
		
		
		Label label2 = new Label(signInPanel, SWT.CENTER| SWT.WRAP);
		label2.setLayoutData(GUIUtils.createFillGridData(1));
		
		GridData tempLayoutData = GUIUtils.createFillGridData(2);
		tempLayoutData.verticalIndent  = 5;
		tempLayoutData.horizontalAlignment = SWT.CENTER;
		tempLayoutData.widthHint = 100;
		
		final Button quitButton = new Button(questionPanel, SWT.PUSH);
		quitButton.setText("Quit");		
		quitButton.setLayoutData(tempLayoutData);
		quitButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Control[] children = headLine.getChildren();
				for (Control control : children) {
					control.dispose();
				}
				
				Control[] children2 = signInPanel.getChildren();
				for (Control control : children2)
				{
					control.dispose();
				}
				
				Control[] children3 = questionPanel.getChildren();
				
				for (Control control : children3) 
				{
					control.dispose();
				}
				
				headLine.dispose();
				signInPanel.dispose();
				questionPanel.dispose();
				
				createMainScreen();
			}
		});
		
		questionPanel.getParent().layout();
		
	}

	/**
	 * Opens the main window and executes the event loop of the application
	 */
	private void runApplication() 
	{
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		boldFont.dispose();
	}
}
