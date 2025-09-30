import React, { useContext, useState } from "react";
import "./CartSummary.css";
import { AppContext } from "../../context/AppContext";
import ReceiptPopup from "../ReceiptPopup/ReceiptPopup";
import { loadStripe } from "@stripe/stripe-js";
import { createOrder, deleteOrder } from "../../service/OrderService";
import toast from "react-hot-toast";
import { createStripeOrder, verifyPayment } from "../../service/PaymentService";
import { AppConstants } from "../../util/constants";

const CartSummary = ({
  customerName,
  mobileNumber,
  setCustomerName,
  setMobileNumber,
}) => {
  const [isProcessing, setIsProcessing] = useState(false);
  const [orderDetails, setOrderDetails] = useState(null);
  const [showPopup, setShowPopup] = useState(false);

  const { cartItems, clearCart } = useContext(AppContext);
  const totalAmount = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );
  const tax = totalAmount * 0.05;
  const grandTotal = totalAmount + tax;

  const clearAll = () => {
    console.log("Clear all called");
    setCustomerName("");
    setMobileNumber("");
    clearCart();
  };

  const placeOrder = () => {
    console.log("Place order is called");
    setShowPopup(true);
    clearAll();
  };

  const handlePrintReceipt = () => {
    window.print();
  };

  const deleteOrderOnFailure = async (orderId) => {
    try {
      await deleteOrder(orderId);
    } catch (error) {
      console.error(error);
      toast.error("Something went wrong");
    }
  };

  const completePayment = async (paymentMode) => {
    // console.log("completePayment called with mode:", paymentMode);
    // console.log("Customer Name:", customerName, "Mobile Number:", mobileNumber);
    // console.log("Cart items:", cartItems);

    if (!customerName || !mobileNumber) {
      toast.error("Please enter customer details!");
      return;
    }

    if (cartItems.length === 0) {
      toast.error("Your cart is empty!");
      return;
    }

    const orderData = {
      customerName: customerName,
      phoneNumber: mobileNumber,
      cartItems,
      subtotal: totalAmount,
      tax,
      grandTotal,
      paymentMethod: paymentMode.toUpperCase(),
    };

    setIsProcessing(true);
    try {
      const response = await createOrder(orderData);
      const savedOrder = response.data;

      if (response.status === 201 && paymentMode === "cash") {
        toast.success("Your cash has been successfully received!");
        setOrderDetails(savedOrder);
      } else if (response.status === 201 && paymentMode === "upi") {
        // console.log("UPI payment selected");
        // console.log("Saved order from backend:", savedOrder);

        try {
          // 1. Ask backend to create Checkout Session
          // console.log("Requesting Stripe Checkout session from backend...");

          const stripeResponse = await createStripeOrder({
            amount: grandTotal,
            currency: "inr",
          });

          // console.log("Stripe response received:", stripeResponse);

          if (!stripeResponse.data || !stripeResponse.data.url) {
            console.error("No URL returned from backend:", stripeResponse.data);
            toast.error("Unable to get Stripe Checkout URL");
            await deleteOrderOnFailure(savedOrder.orderId);
            return;
          }

          // console.log(
          //   "Opening Stripe Checkout in new tab:",
          //   stripeResponse.data.url
          // );

          // 2. Open Stripe Checkout page in a new tab
          const newWindow = window.open(stripeResponse.data.url, "_blank");

          // Check if popup was blocked
          if (
            !newWindow ||
            newWindow.closed ||
            typeof newWindow.closed == "undefined"
          ) {
            toast.error("Popup blocked! Please allow popups and try again.");
            await deleteOrderOnFailure(savedOrder.orderId);
            return;
          }

          // Focus on the new tab
          newWindow.focus();

          toast.success(
            "Payment page opened in new tab. Complete your payment there."
          );
        } catch (error) {
          // console.error("Error while creating Stripe Checkout session:", error);
          toast.error("Something went wrong with payment");
          await deleteOrderOnFailure(savedOrder.orderId);
        }
      }
    } catch (error) {
      // console.error(error);
      toast.error("Something went wrong");
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="mt-2">
      <div className="cart-summary-details">
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Item: </span>
          <span className="text-light">₹{totalAmount.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Tax (5%):</span>
          <span className="text-light">₹{tax.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-4">
          <span className="text-light">Total:</span>
          <span className="text-light">₹{grandTotal.toFixed(2)}</span>
        </div>
      </div>

      <div className="d-flex gap-3">
        <button
          className="btn btn-success flex-grow-1"
          onClick={() => completePayment("cash")}
          disabled={isProcessing}
        >
          {isProcessing ? "Processing..." : "Cash"}
        </button>
        <button
          className="btn btn-primary flex-grow-1"
          onClick={() => completePayment("upi")}
          disabled={isProcessing}
        >
          {isProcessing ? "Processing..." : "UPI"}
        </button>
        <button
          className="btn btn-warning flex-grow-1"
          onClick={placeOrder}
          disabled={isProcessing || !orderDetails}
        >
          Place Order
        </button>
      </div>
      {showPopup && (
        <ReceiptPopup
          orderDetails={{
            ...orderDetails,
            stripeOrderId: orderDetails.paymentDetails?.stripeOrderId,
            stripePaymentId: orderDetails.paymentDetails,
          }}
          onClose={() => setShowPopup(false)}
          onPrint={handlePrintReceipt}
        />
      )}
    </div>
  );
};

export default CartSummary;
