package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Chat;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.holders.ContatoHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TesteActivity extends AppCompatActivity {
    private static final String TAG = "RealtimeDatabaseDemo";

    protected static final Query sChatQuery =
            FirebaseDatabase.getInstance().getReference().child("contatos").orderByChild("idosos").equalTo("555").limitToLast(50);

    @BindView(R.id.messagesList)
    RecyclerView mRecyclerView;

    @BindView(R.id.sendButton)
    Button mSendButton;

    @BindView(R.id.messageEdit)
    EditText mMessageEdit;

    @BindView(R.id.emptyTextView)
    TextView mEmptyListMessage;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attachRecyclerViewAdapter();
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(adapter);
    }


    @OnClick(R.id.sendButton)
    public void onSendClick() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = "User " + uid.substring(0, 6);

        onAddMessage(new Chat(name, mMessageEdit.getText().toString(), uid));

        mMessageEdit.setText("");
    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Contato> options =
                new FirebaseRecyclerOptions.Builder<Contato>()
                        .setQuery(sChatQuery, Contato.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Contato, ContatoHolder>(options) {
            @Override
            public ContatoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ContatoHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_lista, parent, false));
            }

            @Override
            protected void onBindViewHolder(ContatoHolder holder, int position, Contato model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }


    protected void onAddMessage(Chat chat) {
        sChatQuery.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                if (error != null) {
                    Log.e(TAG, "Failed to write message", error.toException());
                }
            }
        });
    }
}
