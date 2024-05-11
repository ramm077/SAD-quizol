import { Box, Button, Modal } from "@mui/material"
import React, { useState } from "react"



const DeleteModal = ({visible, onClose, onConfirm, text, name}) => {

  // if(!visible) return null

  const modalStyle = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 'fit-content',
    bgcolor: "background.paper",
    borderRadius: "10px",
    // border: '2px solid #000',
    // boxShadow: 24,
    p: 4,
  };

  const [reason, setReason] = useState('')
  const types = ['Submit', 'Update', 'Final Submit']

  const handleReasonChange = (e) => setReason(e.target.value)
  
  const handleOnYes = () => {
    onConfirm(reason)
    setReason('')
    onClose()
  }

  return (
    <div>
      <Modal
        open={visible}
        onClose={onClose}
        sx={{
          "& .MuiModal-backdrop": {
            backgroundColor: "rgba(0, 0, 0, 0.2);",
          },
        }}
      >
        <Box sx={modalStyle}>
          <Box sx={{ "& .MuiTextField-root": { m: 1, width: "95%" } }}>
            {/* <div
              style={{
                display: "flex",
                flexDirection: 'row-reverse'
              }}
            >
              <CancelRoundedIcon
                style={{ cursor: "pointer" }}
                onClick={onClose}
              />
            </div> */}
            <div style={{display: 'flex', justifyContent: 'center', fontSize: '22px'}}>
              Are you sure you want to {text} {name}?
            </div>
            {
              !types.includes(text) &&
              <>
                <div style={{marginTop: '15px'}}>Reason</div>
                <div style={{display: 'flex', justifyContent: 'center', marginTop: '5px'}}>
                  <textarea 
                    rows={2} 
                    style={{
                      width: '100%', 
                      padding: '4px', 
                      borderWidth: '1px', 
                      borderColor: '#a9a9a9', 
                      borderRadius: '6px', 
                      fontSize: '14px',
                      lineHeight: '24px',
                      color: '#808080',
                      outline: '2px solid transparent',
                      outlineOffset: '2px'
                    }} 
                    value={reason}
                    onChange={handleReasonChange}
                  />
                </div>
              </>
            }
            <div style={{marginTop: '20px', display: 'flex', justifyContent: 'space-around'}}>
              <Button variant="contained" size="small" color="success" onClick={handleOnYes} disabled={types.includes(text) ? false : (reason === '')}>Yes</Button>
              <Button variant="contained" size="small" color="error" onClick={onClose}>No</Button>
            </div>
          </Box>
        </Box>
      </Modal>
    </div>
  );
}

export default DeleteModal

