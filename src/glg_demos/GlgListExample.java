package glg_demos;

import java.awt.event.*;
import javax.swing.*;
import com.genlogic.*;

//////////////////////////////////////////////////////////////////////////
public class GlgListExample extends GlgJBean
{
   //////////////////////////////////////////////////////////////////////////
   // The main demo class
   //////////////////////////////////////////////////////////////////////////

   static final long serialVersionUID = 0;

   int counter1 = 0;   
   int counter2 = 0;   

   //////////////////////////////////////////////////////////////////////////
   public GlgListExample()
   {
      super();
   }

   // Invoked after hierarchy setup, but before the drawing is displayed. 
   public void VCallback( GlgObject viewport )
   {
      // Initialize list widgets. Define items in the list. 
      InitializeList( "SList" );
      InitializeList( "MList" );
      InitializeList( "EList" );
   }
   
   //////////////////////////////////////////////////////////////////////////
   // This callback is invoked when user interacts with input objects in GLG
   // drawing. 
   //////////////////////////////////////////////////////////////////////////
   public void InputCallback( GlgObject viewport, GlgObject message_obj )
   {
      String
        origin,
        format,
         action;

      super.InputCallback( viewport, message_obj );

      origin = message_obj.GetSResource( "Origin" );
      format = message_obj.GetSResource( "Format" );
      action = message_obj.GetSResource( "Action" );

      // Handle window closing if run stand-alone
      if( format.equals( "Window" ) && action.equals( "DeleteWindow" ) )
        System.exit( 0 );

      if( counter1 > 5 )
        counter1 = 0;
      if( counter2 > 5 )
        counter2 = 0;

      /* List messages. */
      if( format.equals( "List" ) && action.equals( "Select" ) )
      {
         if( origin.equals( "SList" ) )
         {
             String selected_item = message_obj.GetSResource( "SelectedItem" );
             int selected_index = 
                message_obj.GetDResource( "SelectedIndex" ).intValue();
             System.out.println( "SList: Selected Item = " + selected_item +
                             " SelectedIndex = " + selected_index );
         }
         else if( origin.equals( "MList" ) || origin.equals( "EList" ) )
         {

            GlgObject item_state_list = (GlgObject)
               message_obj.SendMessage( "Object/Handler",
                                        "GetItemStateList", 
                                        null, null, null, null );

            GlgObject item_list = (GlgObject)
               message_obj.SendMessage( "Object/Handler",
                                        "GetItemList", 
                                        null, null, null, null );

            if( item_list == null || item_state_list == null )
               return;
            
            System.out.println( "Input callback" );
            System.out.println( origin + ": selected items are" );
            for( int i = 0; i < item_state_list.GetSize(); ++i )
            {
               Integer elem_obj = (Integer) item_state_list.GetElement( i );
               int elem = elem_obj.intValue();
               if( elem == 1 )
                  System.out.println( (String)item_list.GetElement( i ) );
             }
         }
      }
      else if( format.equals( "Button" ) && action.equals( "Activate" ) )
      {
         // Send List widget messages
         if( origin.equals( "ItemMessButton" ) )
         {
            SendItemMessages( "SList", counter1 );
            SendItemMessages( "MList", counter1 );
            SendItemMessages( "EList", counter1 );
            ++counter1;	 
         }
         else if( origin.equals( "SelMessButton" ) )
         {
            // SendSelectionMessages( viewport, "SList", counter2 ); 
            SendSelectionMessages( "MList", counter2 );
            SendSelectionMessages( "EList", counter2 );
            ++counter2;
         }
      }

      // Update the viewport.
      Update();

   }

   //////////////////////////////////////////////////////////////////////////
   // Initialize items list. 
   // InitItemList property of a list object is meant to be used in the
   // GLG Builder only. If it is present, it can be modified using
   // resources InitItemList/Item0, InitItemList/Item1 and so on.
   // 
   // To set items programmatically, AddItem or SetItemList messages should 
   // be used. AddItem message will add items to the current InitItemList list, 
   // if any. SetItemList message will replace the current item list.
   // It requires the use of the Extended API.
   // 
   // In this example, AddItem message is used to add items to the list.
   // Since the InitItemList property of the list widgets in the drawing
   // are not empty, the items will be added to the current item list.
   // If the user wants to define an entire item list in the application,
   // without the use of the Extended API, the list widgets in the drawing
   // should not have any items in the InitItemList property.
   //////////////////////////////////////////////////////////////////////////
   void InitializeList( String obj_name )
   {
      GlgObject viewport = GetViewport();
      String res_name = GlgObject.ConcatResNames( obj_name, "Handler" );
   
      viewport.SendMessage( res_name, "AddItem", "Dallas", 
                            new Integer(GlgObject.BOTTOM), null, null );

      viewport.SendMessage( res_name, "AddItem", "New York", 
                            new Integer(GlgObject.BOTTOM), null, null );

      viewport.SendMessage( res_name, "AddItem", "Boston", 
                            new Integer(GlgObject.BOTTOM), null, null );

      viewport.SendMessage( res_name, "AddItem", "Chicago", 
                            new Integer(GlgObject.BOTTOM), null, null );

      viewport.SendMessage( res_name, "UpdateItemList", 
                            null, null, null, null );

   }

   //////////////////////////////////////////////////////////////////////////
   // Process the following List widget messages:
   // SetInitItemList 
   // SetItemList
   // GetItemList
   // GetItemCount
   // AddItem
   // DeleteItem
   // UpdateItemList
   //////////////////////////////////////////////////////////////////////////
   void SendItemMessages( String obj_name, int case_index )
   {
      GlgObject viewport = GetViewport();
      String handler_res = GlgObject.ConcatResNames( obj_name, "Handler" );
      int i;

      switch( case_index )
      {
      case 0:
         // SetInitItemList
         SetSResource( "MessageString", "SetInitItemList" );
         Update();

         SetSResource( GlgObject.ConcatResNames( obj_name, "InitItemList/Item0" ), 
                      "Green" );
         viewport.SendMessage( handler_res, "SetInitItemList", 
                              null, null, null, null );
         System.out.println( "Message SetInitItemList, set Item0 to Green" );
         break;

      case 1:
         // SetItemList 
         SetSResource( "MessageString", "SetItemList" );
         Update();
         
         GlgObject new_item_list = 
            new GlgDynArray( GlgObject.STRING, 0, 0 );

         new_item_list.AddObjectToBottom( "Red" );
         new_item_list.AddObjectToBottom( "Green" );
         new_item_list.AddObjectToBottom( "Yellow" );
         new_item_list.AddObjectToBottom( "Blue" );
         new_item_list.AddObjectToBottom( "Purple" );

         viewport.SendMessage( handler_res, "SetItemList", 
                               new_item_list, null, null, null );

         System.out.println( obj_name + " Message SetItemList, set items to Red,Green,Yellow,Blue,Purple" );
         break;

      case 2:
         // GetItemList
         SetSResource( "MessageString", "GetItemList" );
         Update();
         
         GlgObject item_list = (GlgObject)
            viewport.SendMessage( handler_res, "GetItemList", 
                                  null, null, null, null );
         if( item_list == null)
            return;

         System.out.println( obj_name + " Message GetItemList, list items are:" );
         for( i =0; i < item_list.GetSize(); ++i )
         {
               String elem;
               elem = (String) item_list.GetElement( i );
               System.out.println( elem );
         }

         break;

      case 3:
         // GetItemCount 
         SetSResource( "MessageString", "GetItemCount" );
         Update();

         Integer num_items = (Integer)
            viewport.SendMessage( handler_res, "GetItemCount", 
                                  null, null, null, null );
         System.out.println( obj_name + " Message GetItemCount, number of items = " +
                         num_items );
         break;

      case 4:
         // AddItem. Adds an item to the bottom of the list.
         SetSResource( "MessageString", "AddItem" );
         Update();

         viewport.SendMessage( handler_res, "AddItem", "Magenda", 
                               new Integer(GlgObject.TOP), null, null );
         viewport.SendMessage( handler_res, "UpdateItemList", 
                               null, null, null, null );

         System.out.println( obj_name + " Message AddItem, add item Magenda to the top of list" );
         break;

      case 5:
         // DeleteItem
         SetSResource( "MessageString", "DeleteItem" );
         Update();

         viewport.SendMessage( handler_res, "DeleteItem", 
                               new Integer(GlgObject.BOTTOM),
                               null, null, null );
         viewport.SendMessage( handler_res, "UpdateItemList", 
                               null, null, null, null );
      
         System.out.println( obj_name + " Message DeleteItem, delete last item" );
         break;

       default:
          SetSResource( "MessageString", "" );
          Update();
          break;
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // Process the following List widget messages:
   // ResetAllItemsState
   // SetItemStateList
   // GetItemStateList
   // GetItemState
   // SetItemState
   // GetSelectedItemList
   //////////////////////////////////////////////////////////////////////////
   void  SendSelectionMessages( String obj_name, int case_index )
   {
      GlgObject viewport = GetViewport();
      String handler_res = GlgObject.ConcatResNames( obj_name, "Handler" );
      int i;

      switch( case_index )
      {
         case 0: 
            // GetItemState. Get item state of the 2nd element.
            SetSResource( "MessageString", "GetItemState" );
            Update();

            Integer item_state = (Integer)
               viewport.SendMessage( handler_res, "GetItemState", 
                                     new Integer(2), null, null, null );
            System.out.println( obj_name + " Message GetItemState, item 2 state is "
                                 + item_state.intValue() );
            break;

          case 1:  
             // GetItemStateList
             SetSResource( "MessageString", "GetItemStateList" );
             Update();

             GlgObject item_state_list = (GlgObject)
                viewport.SendMessage( handler_res, "GetItemStateList", 
                                      null, null, null, null );

             // Traverse the list to find the selected items.
             System.out.println( obj_name + 
                   " Message GetItemStateList, indexes of selected items are:" );
      
             for( i = 0; i < item_state_list.GetSize(); ++i )
             {
                Integer elem_obj = (Integer) item_state_list.GetElement( i );
                int elem = elem_obj.intValue();
                
                if( elem == 1 )
                   System.out.println( i );
             }
             break;

          case 2:  
             // SetItemState. Set 3rd item's state to 1 (selected) 
             SetSResource( "MessageString", "SetItemState" );
             Update();

             viewport.SendMessage( handler_res,	"SetItemState", 
                                   new Integer(3), new Integer(1), null, null );
             Update();

             System.out.println( obj_name + 
                                 " Message SetItemState, select the 3rd item" ); 
             break;

           case 3:  
              // SetItemStateList
              SetSResource( "MessageString", "SetItemStateList" );
              Update();

              Integer num_items = (Integer)
                 viewport.SendMessage( handler_res, "GetItemCount", 
                                  null, null, null, null );

              GlgObject new_item_state_list = 
                 new GlgDynArray( GlgObject.INT_VALUE, 0, 0 );

              for( i = 0; i < num_items.intValue(); ++i )
              {
                 // Select items with odd indexes
                 int state;
                 state = ( (i % 2) > 0 ? 1 : 0 );
                 new_item_state_list.AddObjectToBottom( new Integer(state) );
              }
              
              viewport.SendMessage( handler_res, "SetItemStateList",
                                    new_item_state_list, 
                                    null, null, null );
              System.out.println( obj_name + 
                      " Message SetItemStateList, select items with odd indexes" );

              // Check the current ItemStateList to make sure it was
              // set correctly by the previous SetItemStateList message.
              item_state_list = (GlgObject)
                 viewport.SendMessage( handler_res, "GetItemStateList", 
                                       null, null, null, null );

              // Traverse the list to find the selected items.
              System.out.println( obj_name + 
                                  " Message GetItemStateList, selected items:" );

              for( i = 0; i < item_state_list.GetSize(); ++i )
              {
                 Integer elem_obj = (Integer) item_state_list.GetElement( i );
                 int elem = elem_obj.intValue();
                 if( elem == 1 )
                    System.out.println( i );
              }
              break;

           case 4:  
              // GetSelectedItemList
              SetSResource( "MessageString", "GetSelectedItemList" );
              Update();
              
              GlgObject selected_item_list = (GlgObject)
                 viewport.SendMessage( handler_res, "GetSelectedItemList",
                                       null, null, null, null );
              if( selected_item_list == null )
                 return;
              
              System.out.println( obj_name + 
                                  "Message GetSelectedItemList, selected items:" );
              for( i = 0; i < selected_item_list.GetSize(); ++i )
              {
                 String elem;
                 elem = (String) selected_item_list.GetElement( i );
                 System.out.println( elem );
              }
   
              break;

          case 5:  
             // ResetAllItemsState
             SetSResource( "MessageString", "RestAllItemsState" );
             Update();

             viewport.SendMessage( handler_res, "ResetAllItemsState",
                                   null, null, null, null );
             
             System.out.println( "Message ResetAllItemsState\n" );
             break;

          default:
             SetSResource( "MessageString", "" );
             Update();
             break;
      
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // For use as a stand-alone application
   //////////////////////////////////////////////////////////////////////////
   public static void main( final String arg[] )
   {
      SwingUtilities.
        invokeLater( new Runnable(){ public void run() { Main( arg ); } } );
   }

   //////////////////////////////////////////////////////////////////////////
   public static void Main( final String arg[] )
   {
      class DemoQuit extends WindowAdapter
      {
         public void windowClosing( WindowEvent e ) { System.exit( 0 ); }
      } 

      JFrame frame = new JFrame();
      frame.setResizable( true );
      frame.setSize( 600, 600 );
      frame.setLocation( 20, 20 );
      frame.addWindowListener( new DemoQuit() );

      GlgListExample animation = new GlgListExample();      
      frame.getContentPane().add( animation );
      frame.setVisible( true );

      // Set a GLG drawing to be displayed in the GLG bean
      // Set after layout negotiation has finished.
      // Setting the drawing triggers the ReadyCallback which starts updates.
      //
      animation.SetDrawingName( "list.g" );
   }
}
