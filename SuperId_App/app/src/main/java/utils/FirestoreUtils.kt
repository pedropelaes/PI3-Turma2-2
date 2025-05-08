package utils

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

fun getUsuarioRef(db: FirebaseFirestore, uid: String) =
    db.collection("users").document(uid)

fun getCategoriasRef(db: FirebaseFirestore, uid: String) =
    getUsuarioRef(db, uid).collection("categorias")

fun getSenhasRefDireto(db: FirebaseFirestore, uid: String, categoriaId: String): CollectionReference =
    getCategoriasRef(db, uid).document(categoriaId).collection("senhas")

fun getSenhasRef(
    db: FirebaseFirestore,
    uid: String,
    categoria: String,
    onSuccess: (CollectionReference) -> Unit,
    onFailure: (Exception) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("categorias")
        .whereEqualTo("nome", categoria)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val categoriaId = snapshot.documents.first().id
                val senhasRef = db.collection("users")
                    .document(uid)
                    .collection("categorias")
                    .document(categoriaId)
                    .collection("senhas")
                onSuccess(senhasRef)
            } else {
                onFailure(Exception("Categoria '$categoria' não encontrada"))
            }
        }
        .addOnFailureListener(onFailure)
}