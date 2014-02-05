// @author Andrew Gulla (100395486)
package ca.uoit.AndrewGulla.notepad;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NotepadEdit extends Activity {
	private NotesAdapter mDbHelper;
	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	
	//Initializes the new layout and prepares to input new note into database
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesAdapter(this);
	    mDbHelper.open();
		setContentView(R.layout.notepad_edit);
		setTitle(R.string.edit_note);
		//Uses the XML EditText ID's
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		Button confirmButton = (Button) findViewById(R.id.save);
		
		mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesAdapter.KEY_ROWID)
                                    : null;
        }
        
        populateFields();
		
        //Button that confirms the end of the editing process. 
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
			    setResult(RESULT_OK);
			    finish();
			}
		});
	}
	
	private void populateFields() {
	    if (mRowId != null) {
	        Cursor note = mDbHelper.fetchNote(mRowId);
	        startManagingCursor(note);
	        mTitleText.setText(note.getString(
	                    note.getColumnIndexOrThrow(NotesAdapter.KEY_TITLE)));
	        mBodyText.setText(note.getString(
	                note.getColumnIndexOrThrow(NotesAdapter.KEY_BODY)));
	    }
	}
	
	//SAVES STATES OF PROGRAM
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesAdapter.KEY_ROWID, mRowId);
    }
	
	protected void onPause() {
        super.onPause();
        saveState();
    }
	
	protected void onResume() {
        super.onResume();
        populateFields();
    }
	
	private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String date = "";

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, date);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, date);
        }
    }
}
