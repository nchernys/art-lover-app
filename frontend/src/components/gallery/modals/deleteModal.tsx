type DeleteModalProps = {
  onDeleteModal: (id: string, title: string) => void;
  onDelete: (id: string) => Promise<void>;
  title: string;
  id: string;
};

export function DeleteModal({
  onDeleteModal,
  onDelete,
  title,
  id,
}: DeleteModalProps) {
  return (
    <div className="gallery-delete-modal">
      <div>
        Are you sure you want to delete <i>{title}</i> from your gallery?
      </div>
      <div className="btn-group">
        <button onClick={() => onDeleteModal("", "")}>Cancel</button>
        <button onClick={() => onDelete(id)}>OK</button>
      </div>
    </div>
  );
}
